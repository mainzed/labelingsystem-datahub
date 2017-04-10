$(document).ready(function() {

    $('#searchString').on('input',function(e){
        var val = $("#searchString").val();
        // reset
        if (val!=="") {
            // hide all
            for (var item in loadJSON) {
                $("#"+loadJSON[item].id).hide();
            }
            // show by value in search box
            for (var item in loadJSON) {
                if (mode==="labels") {
                    var lower = loadJSON[item].value.toLowerCase();
                    if (lower.indexOf(val) !== -1) {
                        $("#"+loadJSON[item].id).show();
                    }
                } else if (mode==="objects") {
                    var lower1 = loadJSON[item].description.toLowerCase();
                    var lower2 = loadJSON[item].title.toLowerCase();
                    if (lower1.indexOf(val) !== -1 || lower2.indexOf(val) !== -1) {
                        $("#"+loadJSON[item].id).show();
                    }
                } else if (mode==="projects") {
                    var lower1 = loadJSON[item].description.toLowerCase();
                    var lower2 = loadJSON[item].title.toLowerCase();
                    if (lower1.indexOf(val) !== -1 || lower2.indexOf(val) !== -1) {
                        $("#"+loadJSON[item].id).show();
                    }
                } else if (mode==="resources") {
                    var lower = loadJSON[item].label.toLowerCase();
                    if (lower.indexOf(val) !== -1) {
                        console.info(lower);
                        $("#"+loadJSON[item].id).show();
                    }
                }
            }
        } else {
            for (var item in loadJSON) {
                $("#"+loadJSON[item].id).show();
            }
        }
        // init nanoscroller
        $(".nano").nanoScroller();
    });

});
