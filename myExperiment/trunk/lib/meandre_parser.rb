require 'rdf/redland'

class Node
	@uri
	@name
	attr_accessor :uri, :name
end

class Link
	@source
	@target
	attr_accessor :source, :target
end

class Details
	@name
	@desc
  @uri
	attr_accessor :name, :desc, :uri
end

class MeandreParser
	def initialize(rdfstring)
		@world = Redland::librdf_new_world
		Redland::librdf_world_open @world
		@storage = Redland::librdf_new_storage @world, "memory", "store", ""
		raise "failed to create storage" if !@storage
		
		@model = Redland::librdf_new_model @world, @storage, ""
		raise "failed to create model" if !@model
		
		parser = Redland::librdf_new_parser @world, "turtle", "", nil
		raise "failed to create parser" if !parser
		
		base_uri = Redland::librdf_new_uri @world, 'dontsegfault' #this string must not be null or librdf segfaults????
		
		Redland::librdf_parser_parse_string_into_model parser, rdfstring, base_uri, @model
		
		#Redland::librdf_free_parser parser
		#Redland::librdf_free_stream stream
	end

	def find_nodes()
		predicate = Redland::librdf_new_node_from_uri_string @world, 'http://www.w3.org/1999/02/22-rdf-syntax-ns#type'
		object = Redland::librdf_new_node_from_uri_string @world, 'http://www.meandre.org/ontology/instance_configuration'
		search = Redland::librdf_new_statement_from_nodes @world, nil, predicate, object
		stream = Redland::librdf_model_find_statements @model, search
		
		nodes = Array.new
		while Redland::librdf_stream_end(stream) == 0
			node = Node.new
			statement=Redland::librdf_stream_get_object stream
			subject = Redland::librdf_statement_get_subject statement
			uri = Redland::librdf_node_get_uri subject
			
			node_details = get_properties subject
			node.uri = Redland::librdf_uri_to_string uri
			node.name = node_details['http://www.meandre.org/ontology/instance_name']
			
			nodes.push node
			
			Redland::librdf_stream_next stream
		end
		
		nodes
	end
	
	def find_links()
		predicate = Redland::librdf_new_node_from_uri_string @world, 'http://www.w3.org/1999/02/22-rdf-syntax-ns#type'
		object = Redland::librdf_new_node_from_uri_string @world, 'http://www.meandre.org/ontology/data_connector_configuration'
		search = Redland::librdf_new_statement_from_nodes @world, nil, predicate, object
		stream = Redland::librdf_model_find_statements @model, search

		source_predicate = Redland::librdf_new_node_from_uri_string @world, 'http://www.meandre.org/ontology/connector_instance_source'
		target_predicate = Redland::librdf_new_node_from_uri_string @world, 'http://www.meandre.org/ontology/connector_instance_target'

		links = Array.new
		while Redland::librdf_stream_end(stream) == 0
			statement=Redland::librdf_stream_get_object stream
			subject = Redland::librdf_statement_get_subject statement
			source_search = Redland::librdf_new_statement_from_nodes @world, subject, source_predicate, nil
			source_stream = Redland::librdf_model_find_statements @model, source_search
			source_statement = Redland::librdf_stream_get_object source_stream
			source = Redland::librdf_statement_get_object source_statement
			source_uri = Redland::librdf_node_get_uri source
			
			target_search = Redland::librdf_new_statement_from_nodes @world, subject, target_predicate, nil
			target_stream = Redland::librdf_model_find_statements @model, target_search
			target_statement = Redland::librdf_stream_get_object target_stream
			target = Redland::librdf_statement_get_object target_statement
			target_uri = Redland::librdf_node_get_uri target
			
			link = Link.new
			link.source = Redland::librdf_uri_to_string source_uri
			link.target = Redland::librdf_uri_to_string target_uri
			links.push link
			
			Redland::librdf_stream_next stream
		end
		
		links
	end
	
	def make_dot()
		nodes = find_nodes
		links = find_links
		
		dot = ""
		dot += "digraph workflow{\n"
		nodes.each do |node|
			dot += "\"#{node.uri}\" [label=\"#{node.name}\"]\n"
		end

		links.each do |link|
			dot += "\"#{link.source}\" -> \"#{link.target}\"\n"
		end
		dot += "}"
		
		dot
	end
	
	def find_details()
		predicate = Redland::librdf_new_node_from_uri_string @world, 'http://www.w3.org/1999/02/22-rdf-syntax-ns#type'
		object = Redland::librdf_new_node_from_uri_string @world, 'http://www.meandre.org/ontology/flow_component'
		search = Redland::librdf_new_statement_from_nodes @world, nil, predicate, object
		stream = Redland::librdf_model_find_statements @model, search
		
		workflow_statement = Redland::librdf_stream_get_object stream
		workflow = Redland::librdf_statement_get_subject workflow_statement	
	  workflow_uri = Redland::librdf_node_get_uri workflow	
		details = Details.new
		workflow_details = get_properties(workflow)
		details.name = workflow_details['http://www.meandre.org/ontology/name']
		details.desc = workflow_details['http://purl.org/dc/elements/1.1/description']
    details.uri = Redland::librdf_uri_to_string workflow_uri
		details
	end
	
  #serialise to ttl
  def get_ttl()
    serializer = Redland::librdf_new_serializer(@world, 'turtle', 'application/x-turtle', nil)
    return Redland::librdf_serializer_serialize_model_to_string(serializer, nil, @model)
  end

  #sets the input string on a myExperimentInput component
  def set_inputs(inputs)
    details = find_details 
    input_uri = details.uri + 'instance/my-experiment-input/0/property/uri' 
    input_node = Redland::librdf_new_node_from_uri_string(@world, input_uri)
    property_node = Redland::librdf_new_node_from_uri_string(@world, 'http://www.meandre.org/ontology/value')
    value_node = Redland::librdf_new_node_from_literal(@world, inputs)

    search = Redland::librdf_new_statement_from_nodes(@world, input_node, property_node, nil)
    stream = Redland::librdf_model_find_statements(@model, search)
    if Redland::librdf_stream_end(stream) == 0
      statement = Redland::librdf_stream_get_object(stream)
      Redland::librdf_statement_set_object(statement, value_node)
      Redland::librdf_model_add_statement(@model, statement)
    end
  end

  #gets the input string property uri of the PushString component
  def get_inputs()
    details = find_details 
    input_uri = details.uri + 'instance/push-string/0' 
    return input_uri
  end

	#helper method that extracts literals from a given subject
	def get_properties(subject)
		search = Redland::librdf_new_statement_from_nodes @world, subject, nil, nil
		stream = Redland::librdf_model_find_statements @model, search
		
		props = Hash.new
		while Redland::librdf_stream_end(stream) == 0
			statement = Redland::librdf_stream_get_object stream
			predicate = Redland::librdf_statement_get_predicate statement
			object = Redland::librdf_statement_get_object statement
			if Redland::librdf_node_is_literal object
				predicate_uri = Redland::librdf_node_get_uri predicate
				predicate_uri_string = Redland::librdf_uri_to_string predicate_uri
				literal = Redland::librdf_node_get_literal_value object
				props[predicate_uri_string] = literal
			end
			Redland::librdf_stream_next stream
		end
		
		props
	end
end
