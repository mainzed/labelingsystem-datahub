//$(document).ready(function() {

    /*$(document).on('click', '.labelvalue', function() {
        var concept = $(this).attr('v');
        filter = {"concept": concept};
        getDatasets();
    });*/

    // general functions
    var getLabels = function() {
        filter.labels = true;
        console.info(mode, filter);
        $.ajax({
            async: false,
            type: 'GET',
            url: searchURL,
            data: (function(){
                return filter;
            })(),
            error: function(jqXHR, textStatus, errorThrown) {
                console.info(textStatus);
            },
            success: function(response) {
                try {
                    response = JSON.parse(response);
                } catch (e) {}
                // create divs
                $("#b-butswitch").show();
                $("#b-butswitch").html("datasets view");
                $("#langswitch").show();
                $("#contentcontent").empty();
                for (var obj in response) {
                    var div = "<div id='"+response[obj].uri+"' class='label'>";
                    div += "<span class='labelvalue' v='"+response[obj].uri+"'>"+response[obj].value+"</span>";
                    div += "<span class='labellang'>"+response[obj].lang+"</span>";
                    div += "</div>";
                    $("#contentcontent").append(div);
                }
                // output in header
                var filterCopy = filter;
                delete filterCopy["labels"];
                var filters = "";
                for (var item in filterCopy) {
                    filters += " " + item + ":" + filterCopy[item];
                }
                if (filters!=="") {
                    $("#header-info").html("labels for " + filters);
                } else {
                    $("#header-info").html("all labels");
                }
                // init nanoscroller
                $(".nano").nanoScroller();
            }
        });
    }
    var getDatasets = function() {
        filter.labels = false;
        console.info(mode, filter);
        $.ajax({
            async: false,
            type: 'GET',
            url: searchURL,
            data: (function(){
                return filter;
            })(),
            error: function(jqXHR, textStatus, errorThrown) {
                console.info(textStatus);
            },
            success: function(response) {
                try {
                    response = JSON.parse(response);
                } catch (e) {}
                // create divs
                $("#b-butswitch").show();
                $("#b-butswitch").html("label view");
                $("#contentcontent").empty();
                for (var obj in response) {
                    var div = "<div id='"+response[obj].id+"' class='object'>";
                    div += "<div class='objtitle'>"+response[obj].title+"</div>";
                    div += "<div class='objdesc'>"+response[obj].description+"</div>";
                    div += "<div class='objdepiction'><img src='"+response[obj].depiction+"' class='objdepictionimg'></div>";
                    div += "</div>";
                    $("#contentcontent").append(div);
                }
                // output in header
                var filterCopy = filter;
                delete filterCopy["labels"];
                var filters = "";
                for (var item in filterCopy) {
                    filters += " " + item + ":" + filterCopy[item];
                }
                if (filters!=="") {
                    $("#header-info").html("datasets for " + filters);
                } else {
                    $("#header-info").html("all datasets");
                }
                // init nanoscroller
                $(".nano").nanoScroller();
            }
        });
    }

//});
