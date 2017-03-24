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
            for (var obj in response) {
                var div = "<div id='"+response[obj].uri+"' class='label'>";
                div += "<span class='labelvalue'>"+response[obj].value+"</span>";
                div += "<span class='labelproperties'>language: "+response[obj].lang+"<br>datasets: "+response[obj].datasets+"</span>";
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
                if (filters.includes("lat_min")) {
                    filters = "envelope";
                }
                $("#header-info").html(response.length + " label(s) for " + filters);
            } else {
                $("#header-info").html("all label(s) (" + response.length + ")");
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
            // output in header
            var filterCopy = filter;
            delete filterCopy["labels"];
            var filters = "";
            for (var item in filterCopy) {
                filters += " " + item + ":" + filterCopy[item];
            }
            if (filters!=="") {
                if (filters.includes("lat_min")) {
                    filters = "envelope";
                }
                $("#header-info").html(response.length + " dataset(s) for " + filters);
            } else {
                $("#header-info").html("all dataset(s) (" + response.length + ")");
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
                div += "<div class='objvalue'>"+response[obj].title+"</div>";
                div += "<div class='objproperties'>";
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
            // output in header
            var filterCopy = filter;
            delete filterCopy["projects"];
            var filters = "";
            for (var item in filterCopy) {
                filters += " " + item + ":" + filterCopy[item];
            }
            if (filters!=="") {
                if (filters.includes("lat_min")) {
                    filters = "envelope";
                }
                $("#header-info").html(response.length + " project(s) for " + filters);
            } else {
                $("#header-info").html("all project(s) (" + response.length + ")");
            }
            // init nanoscroller
            $(".nano").nanoScroller();
        }
    });
}
