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
            $("#contentcontent").empty();
            /*for (var obj in response) {
                var div = "<div id='"+response[obj].uri+"' class='label'>";
                div += "<span class='labelvalue'>"+response[obj].value+"</span>";
                div += "<span class='labelproperties'>language: "+response[obj].lang+"<br>datasets: "+response[obj].datasets+"</span>";
                div += "</div>";
                $("#contentcontent").append(div);
            }*/
            for (var obj in response) {
                var sublabellength = 18;
                var sublabel = response[obj].value.substring(0,sublabellength);
                if (response[obj].value.length > sublabellength) {
                    sublabel += " [...]";
                }
                var div = "<div id='"+response[obj].uri+"' class='label'>";
                div += "<span class='labelvalue'>"+sublabel+"&nbsp;("+response[obj].lang+")</span>";
                div += "</div>";
                $("#contentcontent").append(div);
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
            $("#contentcontent").empty();
            for (var obj in response) {
                var div = "<div id='"+response[obj].id+"' class='object'>";
                div += "<div class='objvalue'>"+response[obj].title+"</div>";
                div += "<div class='objproperties'>";
                div += "description: "+response[obj].description;
                div += "</div>";
                div += "<div class='objdepiction'><img src='"+response[obj].depiction+"' class='objdepictionimg'></div>";
                div += "<div class='objproperties2'>";
                div += "lat/lon: "+response[obj].lat+"/"+response[obj].lat+" <a href='"+response[obj].coverage+"' target='_blank'>GeoNames</a>";
                div += "<br>";
                div += "timespan: "+response[obj].begin+"-"+response[obj].end+" <a href='"+response[obj].temporal+"' target='_blank'>ChronOntology</a>";
                div += "</div>";
                div += "</div>";
                $("#contentcontent").append(div);
            }
            // init nanoscroller
            $(".nano").nanoScroller();
        }
    });
}

var getProjects = function() {
    filter.projects = true;
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
            $("#contentcontent").empty();
            for (var obj in response) {
                var div = "<div id='"+response[obj].id+"' class='project'>";
                div += "<div class='projectvalue'>"+response[obj].title+"</div>";
                div += "<div class='projectproperties'>";
                div += "description: "+response[obj].description;
                div += "<br>";
                div += "datasets: "+response[obj].datasets;
                div += "<br>";
                div += "creator: <a href='"+response[obj].publisher+"' target='_blank'>"+response[obj].creator+"</a>";
                div += "<br>";
                div += "license: <a href='"+response[obj].license+"' target='_blank'>"+response[obj].license+"</a>";
                div += "<br>";
                div += "data dump: <a href='"+response[obj].dump+"' target='_blank'>"+response[obj].dump+"</a>";
                div += "<br>";
                div += "SPARQL endpoint: <a href='"+response[obj].sparql+"' target='_blank'>"+response[obj].sparql+"</a>";
                div += "</div>";
                div += "</div>";
                $("#contentcontent").append(div);
            }
            // init nanoscroller
            $(".nano").nanoScroller();
        }
    });
}

var getResources = function() {
    $.ajax({
        async: false,
        type: 'GET',
        url: resourcesURL,
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
            var seltype = $("#resourcetype option:selected").val();
            var displayedResources = [];
            if (seltype!=="") {
                for (var item in resources) {
                    if (resources[item].type===seltype) {
                        displayedResources.push(resources[item]);
                    }
                }
            } else {
                displayedResources = resources;
            }
            // sort array
            displayedResourcesCopy = sortArrayByValue(displayedResources,"label","uri");
            // create divs
            $("#contentcontent").empty();
            for (var obj in displayedResourcesCopy) {
                var div = "<div id='"+displayedResourcesCopy[obj].uri+"' class='resource'>";
                var sublabel = displayedResourcesCopy[obj].label.substring(0,20);
                if (displayedResourcesCopy[obj].label.length > 20) {
                    sublabel += " [...]";
                }
                div += "<div class='resourcevalue'>"+sublabel+"</div>";
                div += "<div class='resourcetype'>";
                div += "type: "+displayedResourcesCopy[obj].type;
                div += "</div>";
                div += "<div class='resourceproperties'>";
                var subdescription = displayedResourcesCopy[obj].description.substring(0,200);
                if (displayedResourcesCopy[obj].description.length > 200) {
                    subdescription += " [...]";
                }
                div += "description: "+subdescription;
                div += "</div>";
                div += "</div>";
                $("#contentcontent").append(div);
            }
            // init nanoscroller
            $(".nano").nanoScroller();
        }
    });
}
