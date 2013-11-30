// Place third party dependencies in the lib folder
//
// Configure loading modules from the lib directory,
// except 'app' ones, 
requirejs.config({
    "baseUrl" : ".",
    "paths" : {
        "jquery" : 'jquery.js'
    }
});