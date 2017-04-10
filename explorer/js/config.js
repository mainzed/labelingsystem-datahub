var mode = "labels"; // default: labels
var filter = {"labels": true}; // default: label view
var HOST = "http://ls-dev.i3mainz.hs-mainz.de";
var resourcesURL = HOST + "/datahub/resources";
var searchURL = HOST + "/datahub/search";
var projectsURL = HOST + "/datahub/projects";
var retcatURL = HOST + "/api/v1/retcat";
var map;
var drawnItems;
var editableLayers;
var envelope = false;
var resources;
var loadJSON;

var optsSpin = {
      lines: 13 // The number of lines to draw
    , length: 28 // The length of each line
    , width: 14 // The line thickness
    , radius: 42 // The radius of the inner circle
    , scale: 1 // Scales overall size of the spinner
    , corners: 1 // Corner roundness (0..1)
    , color: '#30395c' // #rgb or #rrggbb or array of colors
    , opacity: 0.25 // Opacity of the lines
    , rotate: 0 // The rotation offset
    , direction: 1 // 1: clockwise, -1: counterclockwise
    , speed: 1 // Rounds per second
    , trail: 60 // Afterglow percentage
    , fps: 20 // Frames per second when using setTimeout() as a fallback for CSS
    , zIndex: 2e9 // The z-index (defaults to 2000000000)
    , className: 'spinner' // The CSS class to assign to the spinner
    , top: '50%' // Top position relative to parent
    , left: '50%' // Left position relative to parent
    , shadow: false // Whether to render a shadow
    , hwaccel: false // Whether to use hardware acceleration
    , position: 'relative' // Element positioning
};

var sortArrayByValue = function(array,param,param2) {
    // tmp copy for sorting
    var arrayCopy = array;
    // set label to lowercase
    for (var item in array) {
        arrayCopy[item][param].toLowerCase();
    }
    // sort
    function sortByKey(array, key) {
        return array.sort(function(a, b) {
            var x = a[key]; var y = b[key];
            return ((x < y) ? -1 : ((x > y) ? 1 : 0));
        });
    }
    arrayCopy = sortByKey(arrayCopy, param);
    // set original label
    for (var item in array) {
        if (array[item][param2] === arrayCopy[item][param2]) {
            arrayCopy[item][param] = array[item][param];
        }
    }
    return arrayCopy;
}
