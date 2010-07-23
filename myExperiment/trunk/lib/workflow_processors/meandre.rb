# myExperiment: lib/workflow_processors/meandre.rb
#
# Copyright (c) 2008 University of Manchester and the University of Southampton.
# See license.txt for details.

module WorkflowProcessors
  
 require 'file_upload'
 require 'meandre_parser'
 require 'zip/zip'

  class Meandre < WorkflowProcessors::Interface
    Mime::Type.register "application/octet-stream", :meandre
    
    # Begin Class Methods
    
    # These: 
    # - provide information about the Workflow Type supported by this processor,
    # - provide information about the processor's capabilites, and
    # - provide any general functionality.
  
    # MUST be unique across all processors
    def self.display_name 
      "Meandre"
    end
    
    def self.display_data_format
      "Meandre"
    end
    
    # All the file extensions supported by this workflow processor.
    # Must be all in lowercase.
    def self.file_extensions_supported
      [ "mau" ]
    end
    
    def self.can_determine_type_from_file?
      true
    end
    
    def self.recognised?(file)
	  true
    end
    
    def self.can_infer_metadata?
      true
    end
    
    def self.can_generate_preview_image?
      true
    end
    
    def self.can_generate_preview_svg?
      true
    end
    
    # End Class Methods
    
    
    # Begin Object Initializer

    def initialize(workflow_definition)
      super(workflow_definition)
	  i = File.new("/tmp/meandre_zip",'w+');
	  i.write(workflow_definition);
	  i.close();
	  rdf = ""
	  Zip::ZipFile.open(i.path) do |zipfile|
			rdf = zipfile.read("repository/repository.ttl")
	  end
	  @parser = MeandreParser.new(rdf)
	  @details = @parser.find_details
    end

    # End Object Initializer
    
    
    # Begin Instance Methods
    
    # These provide more specific functionality for a given workflow definition, such as parsing for metadata and image generation.
    
    def get_title
		return @details.name;
	end
    
    def get_description
		return @details.desc
    end
    
    def get_preview_image
		#return nil if @parser.nil? || RUBY_PLATFORM =~ /mswin32/

		title = @details.name
		filename = title.gsub(/[^\w\.\-]/,'_').downcase
		i = Tempfile.new("image")
		i.write @parser.make_dot
		i.close(false)

		img = StringIO.new(`dot -Tpng #{i.path}`)
		img.extend FileUpload
		img.original_filename = "#{filename}.png"
		img.content_type = "image/png"
		
		img
    end

    def get_preview_svg
	#return nil if @parser.nil? || RUBY_PLATFORM =~ /mswin32/

		title = @details.name
		filename = title.gsub(/[^\w\.\-]/,'_').downcase
		i = Tempfile.new("image")
		i.write @parser.make_dot
		i.close(false)

		img = StringIO.new(`dot -Tsvg #{i.path}`)
		img.extend FileUpload
		img.content_type = "image/svg+xml"
		img.original_filename = "#{filename}.svg"
		
		img
    end

    def get_workflow_model_object
		@parser
	end
    
    def get_workflow_model_input_ports
    end
    
    def get_search_terms
    end

    def get_components
    end

    # End Instance Methods
  end
end
