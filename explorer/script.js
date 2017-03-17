$(document).ready(function() {
    // fill language dropdown
    $('#langswitch').on('change', function() {
        var lang = $("#langswitch").val();
        $.ajax({
            async: false,
            type: 'GET',
            url: "http://ls-dev.i3mainz.hs-mainz.de/datahub/labels",
            data: (function(){
                if (lang !== "") {
                    return {"lang": lang};
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
                    //div += response[obj].value;
                    div += "<span class='labelvalue' v='"+response[obj].uri+"'>"+response[obj].value+"</span>";
                    div += "<span class='labellang'>"+response[obj].lang+"</span>";
                    div += "</div>";
                    $("#labelsection").append(div);
                }
            }
        });
    });
    // load all labels
    $.ajax({
        async: false,
        type: 'GET',
        url: "http://ls-dev.i3mainz.hs-mainz.de/datahub/labels",
        //data: {query: query, format: 'json'},
        error: function(jqXHR, textStatus, errorThrown) {
            console.info(textStatus);
        },
        success: function(response) {
  			try {
  				response = JSON.parse(response);
  			} catch (e) {}
            // create divs
            for (var obj in response) {
                var div = "<div id='"+response[obj].uri+"' class='label'>";
                //div += response[obj].value;
                div += "<span class='labelvalue' v='"+response[obj].uri+"'>"+response[obj].value+"</span>";
                div += "<span class='labellang'>"+response[obj].lang+"</span>";
                div += "</div>";
                $("#labelsection").append(div);
            }
        }
    });
    //labelvalue on click
    $(document).on('click', '.labelvalue', function() {
        var v = $(this).attr('v');
        console.log(v);
        $.ajax({
            async: false,
            type: 'GET',
            url: "http://ls-dev.i3mainz.hs-mainz.de/datahub/search",
            data: {concept: v},
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
                    //div += response[obj].value;
                    div += "<div class='objtitle'>"+response[obj].title+"</div>";
                    div += "<div class='objdesc'>"+response[obj].description+"</div>";
                    div += "<div class='objdepiction'><img src='"+response[obj].depiction+"' class='objdepictionimg'></div>";
                    div += "</div>";
                    $("#labelsection").append(div);
                }
            }
        });
    });
});
