require 'rdf/redland'

class CollectionParser
	def initialize(rdfstring)
		@world = Redland::librdf_new_world
		Redland::librdf_world_open @world
		@storage = Redland::librdf_new_storage @world, "memory", "store", ""
		raise "failed to create storage" if !@storage
		
		@model = Redland::librdf_new_model @world, @storage, ""
		raise "failed to create model" if !@model
		
		parser = Redland::librdf_new_parser @world, "rdfxml", "", nil
		raise "failed to create parser" if !parser
		
		base_uri = Redland::librdf_new_uri @world, 'dontsegfault' #this string must not be null or librdf segfaults????
		
		Redland::librdf_parser_parse_string_into_model parser, rdfstring, base_uri, @model
		
		#Redland::librdf_free_parser parser
		#Redland::librdf_free_stream stream
	end

	def find_aggregate()
		predicate = Redland::librdf_new_node_from_uri_string @world, 'http://www.w3.org/1999/02/22-rdf-syntax-ns#type'
		object = Redland::librdf_new_node_from_uri_string @world, 'http://www.openarchives.org/ore/terms/Aggregate'
		search = Redland::librdf_new_statement_from_nodes @world, nil, nil, object
		stream = Redland::librdf_model_find_statements @model, search

    statement=Redland::librdf_stream_get_object stream
    aggregate=Redland::librdf_statement_get_object statement
    return aggregate
	end
	
	def find_signals()
		predicate = Redland::librdf_new_node_from_uri_string @world, 'http://www.openarchives.org/ore/terms/aggregates'
    subject = find_aggregate();
		search = Redland::librdf_new_statement_from_nodes @world, nil, predicate, nil
		stream = Redland::librdf_model_find_statements @model, search

		signal_uris = Array.new
		while Redland::librdf_stream_end(stream) == 0
			statement=Redland::librdf_stream_get_object stream
			object = Redland::librdf_statement_get_object statement
      uri = Redland::librdf_node_get_uri object
      uri_string = Redland::librdf_uri_to_string uri
      signal_uris << uri_string
      Redland::librdf_stream_next stream
		end

    return signal_uris
	end

end	
