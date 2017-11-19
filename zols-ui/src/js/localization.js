'use strict';
(function($) {



    if( typeof google !== "undefined" ) {
      // Load the Google Transliterate API
      google.load("elements", "1", {
          packages: "transliteration"
      });

      function onLoad() {

          console.log("Google API Loaded");
      }

      google.setOnLoadCallback(onLoad);
    }




}(jQuery));
