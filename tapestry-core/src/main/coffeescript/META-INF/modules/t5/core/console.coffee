# Copyright 2012-2013 The Apache Software Foundation
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http:#www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# ## t5/core/console
#
# A wrapper around the native console, when it exists.
define ["./dom", "underscore"],
  (dom, _) ->

    nativeConsole = {}
    floatingConsole = null
    messages = null

    noFilter = -> true

    filter = noFilter

    updateFilter = (text) ->
      if text is ""
        filter = noFilter
        return

      text = text.toLowerCase()

      filter = (e) -> e.text().toLowerCase().indexOf(text) >= 0

      return

    forceFloating = (dom.body.attribute "data-floating-console") == "true"

    try
      # FireFox will throw an exception if you even access the console object and it does
      # not exist. Wow!
      nativeConsole = console
    catch e

    # _internal_: displays the message inside the floating console, creating the floating
    # console as needed.
    display = (className, message) ->

      dom.withReflowEventsDisabled ->

        unless floatingConsole
          floatingConsole = dom.create
            class: "tapestry-console",
            """
              <div class="message-container"></div>
              <div class="row-fluid">
                <button data-action="clear" class="btn btn-mini"><i class="icon-remove"></i> Clear Console</button>
                <button data-action="enable" class="btn btn-mini" disabled="true"><i class="icon-play"></i> Enable Console</button>
                <button data-action="disable" class="btn btn-mini"><i class="icon-pause"></i> Disable Console</button>
                <input class="search-query input-xlarge" size="40" placeholder="Filter console content">
              </div>
              """

          dom.body.prepend floatingConsole

          messages = floatingConsole.findFirst ".message-container"

          floatingConsole.on "click", "[data-action=clear]", ->
            floatingConsole.hide()
            messages.update ""

          floatingConsole.on "click", "[data-action=disable]", ->

            @attribute "disabled", true
            floatingConsole.findFirst("[data-action=enable]").attribute "disabled", false

            messages.hide()

            return false

          floatingConsole.on "click", "[data-action=enable]", ->

            @attribute "disabled", true
            floatingConsole.findFirst("[data-action=disable]").attribute "disabled", false

            messages.show()

            return false

          floatingConsole.on "change keyup", ".search-query", ->
            updateFilter @value()

            for e in messages.children()
              visible = filter e

              e[if visible then "show" else "hide"]()

            return false

        div = dom.create
          class: className,
          _.escape message

          # Should really filter on original message, not escaped.

        unless filter div
          div.hide()

        messages.append div

        # Make sure the console is visible, even if disabled (and even if the new content is hidden).
        floatingConsole.show()

        # A slightly clumsy way to ensure that the container is scrolled to the bottom.
        _.delay -> messages.element.scrollTop = messages.element.scrollHeight

    level = (className, consolefn) ->
      (message) ->
        # consolefn may be null if there's no console; under IE it may be non-null, but not a function.
        # For some testing, it is nice to force the floating console to always display.

        if forceFloating or (not consolefn)
          # Display it floating. If there's a real problem, such as a failed Ajax request, then the
          # client-side code should be alerting the user in some other way, and not rely on them
          # being able to see the logged console output.
          display className, message

          return unless forceFloating

        if _.isFunction consolefn
          # Use the available native console, calling it like an instance method
          consolefn.call console, message
        else
          # And IE just has to be different. The properties of console are callable, like functions,
          # but aren't proper functions that work with `call()` either.
          consolefn message

        return

    exports =
      info: level "info", nativeConsole.info
      warn: level "warn", nativeConsole.warn
      error: level "error", nativeConsole.error

      # Determine whether debug is enabled by checking for the necessary attribute (which is missing
      # in production mode).
      debugEnabled: (document.documentElement.getAttribute "data-debug-enabled")?

    # When debugging is not enabled, then the debug function becomes a no-op.
    exports.debug =
      if exports.debugEnabled
        # If native console available, go for it.  IE doesn't have debug, so we use log instead.
        level "debug", (nativeConsole.debug or nativeConsole.log)
      else
        ->

    # This is also an aid to debugging; it allows arbitrary scripts to present on the console; when using Geb
    # and/or Selenium, it is very useful to present debugging data right on the page.
    window.t5console = exports

    requirejs.onError = (err) ->

      message = "RequireJS error: #{err.requireType}"

      if err.message
        message += """: #{err.message}"""

      if err.requireType
        modules = err.requireModules
        if modules and modules.length > 0
          message += """, modules #{modules.join(", ")}"""

      exports.error message


    # Return the exports; we keep a reference to it, so we can see exports.DURATION, even
    # if some other module imports this one and modifies that property.
    return exports