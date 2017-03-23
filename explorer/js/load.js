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
                div += "<div class='objtitle'>"+response[obj].title+"</div>";
                div += "<div class='objdesc'>"+response[obj].description+"</div>";
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
