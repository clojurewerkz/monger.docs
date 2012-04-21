module Jekyll

  #
  # Render file & gist example
  #
  # Usage:
  #
  # {% example "file_to_expand", gist_id %} =>
  #
  # Raises: Liquid::SyntaxError
  class Example < ::Liquid::Tag

    Syntax = /(#{::Liquid::Expression}+), (#{::Liquid::Expression}+)?/

    def initialize(tag_name, markup, tokens)
      if markup =~ Syntax
        @name = $1
        @gist_id = $2
        @options = {}
        markup.scan(::Liquid::TagAttributes) { |key, value| @options[key.to_sym] = value.gsub(/"|'/, '') }
      else
        raise ::Liquid::SyntaxError.new("Syntax Error in 'link' - Valid syntax: gist <id> <options>")
      end

      super
    end

    def render(context)
      %{<pre>#{File.open("./examples/#{@name}", "rb").read}</pre><span class="help-block">(if the example above isn't displayed, see this "gist":https://gist.github.com/#{@gist_id})</span>}
    end
  end

end


Liquid::Template.register_tag('example', Jekyll::Example)
