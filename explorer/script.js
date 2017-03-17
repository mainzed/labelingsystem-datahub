var mode = "labels"; // default: labels
var filter = {}; // default: empty object
var searchURL = "http://ls-dev.i3mainz.hs-mainz.de/datahub/search";
var labelsURL = "http://ls-dev.i3mainz.hs-mainz.de/datahub/labels";

$(document).ready(function() {
    // fill language dropdown
    $('#langswitch').on('change', function() {
        var lang = $("#langswitch").val();
        filter = {"lang": lang};
        getLabels();
    });
    //labelvalue on click (get datasets)
    $(document).on('click', '.labelvalue', function() {
        var concept = $(this).attr('v');
        filter = {"concept": concept};
        getDatasets();
    });
    // change view from labels to objects
    $('#butswitch').on('click', function() {
        if (mode==="labels") { // get objects
            getDatasets();
        } else if (mode==="objects") { // get labels
            getLabels();
        }
    });

    // general functions
    var getLabels = function() {
        $.ajax({
            async: false,
            type: 'GET',
            url: labelsURL,
            data: (function(){
                if (filter.lang) {
                    return {"lang": filter.lang};
                }
            })(),
            error: function(jqXHR, textStatus, errorThrown) {
                console.info(textStatus);
            },
            success: function(response) {
                try {
                    response = JSON.parse(response);
                } catch (e) {}
                // create divs
                $("#labelsection").empty();
                for (var obj in response) {
                    var div = "<div id='"+response[obj].uri+"' class='label'>";
                    div += "<span class='labelvalue' v='"+response[obj].uri+"'>"+response[obj].value+"</span>";
                    div += "<span class='labellang'>"+response[obj].lang+"</span>";
                    div += "</div>";
                    $("#labelsection").append(div);
                }
                mode = "labels";
            }
        });
    }
    var getDatasets = function() {
        $.ajax({
            async: false,
            type: 'GET',
            url: searchURL,
            data: (function(){
                if (filter.concept) {
                    return {"concept": filter.concept};
                }
            })(),
            error: function(jqXHR, textStatus, errorThrown) {
                console.info(textStatus);
            },
            success: function(response) {
                try {
                    response = JSON.parse(response);
                } catch (e) {}
                // create divs
                $("#labelsection").empty();
                for (var obj in response) {
                    var div = "<div id='"+response[obj].id+"' class='object'>";
                    div += "<div class='objtitle'>"+response[obj].title+"</div>";
                    div += "<div class='objdesc'>"+response[obj].description+"</div>";
                    div += "<div class='objdepiction'><img src='"+response[obj].depiction+"' class='objdepictionimg'></div>";
                    div += "</div>";
                    $("#labelsection").append(div);
                }
                mode = "objects";
            }
        });
    }

    // load all labels
    getLabels();
});
