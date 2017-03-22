var loadLandingPage = function() {
    $("#contentcontent").empty();
    var div = "<div id='landingpage'>";
    div += "<p style='font-size:30px;font-weight: 700;color:black;'>This is the landing page.</p>";
    div += "</div>";
    $("#contentcontent").append(div);
    $(".nano").nanoScroller();
}

var loadFooter = function() {
    $("#footer").empty();
    var div = "<p>Labeling System+ Lucy Edition - build 22/03/2017</p>";
    $("#footer").append(div);
}

$(document).ready(function() {

    var init = function() {
        $("#b-butswitch").html("datasets view");
        loadProjects();
        loadPublisher();
        loadLanguages();
        loadLandingPage();
        loadFooter();
        $("#header-info").html("Labeling System DataHub Explorer");
        // init time slider
        $("#slider-time-range").slider({
            range: true,
            min: -250,
            max: 350,
            values: [-100,250],
            slide: function(event, ui) {
                $("#year").val("begin:" + ui.values[0] + " end:" + ui.values[1]);
            }
        });
        $("#year").val("begin:" + $("#slider-time-range").slider("values", 0) + " end:" + $("#slider-time-range").slider("values", 1));
        // init mapid
        map = L.map('map').setView([51.505, -0.09], 3);
        L.tileLayer('http://{s}.tiles.mapbox.com/v3/isawnyu.map-knmctlkh/{z}/{x}/{y}.png', {
			maxZoom: 15,
            attribution: "see here..."
            /*attribution: "Tiles &copy; <a href='http://mapbox.com/' target='_blank'>MapBox</a> | " +
					"Data &copy; <a href='http://www.openstreetmap.org/' target='_blank'>OpenStreetMap</a> and contributors, CC-BY-SA |" +
					" Tiles and Data &copy; 2013 <a href='http://www.awmc.unc.edu' target='_blank'>AWMC</a>" +
					" <a href='http://creativecommons.org/licenses/by-nc/3.0/deed.en_US' target='_blank'>CC-BY-NC 3.0</a>"*/
        }).addTo(map);
        var marker = L.marker([51.505, -0.09]).addTo(map);
        // set leaflet draw
        var drawnItems = new L.FeatureGroup();
        var editableLayers = new L.FeatureGroup();
        map.addLayer(editableLayers);
        map.addLayer(drawnItems);
		L.drawLocal.draw.toolbar.buttons.rectangle = 'Draw a boundingbox for selection';
        L.drawLocal.draw.handlers.rectangle.tooltip.start = 'Not telling...';
        var options = {
            position: 'topleft',
            draw: {
                polyline: false,
                polygon: false,
                circle: false,
                marker: false,
                rectangle: {
                    metric: true,
                    shapeOptions: {
                        color: '#0000FF'
                    }
                }
            },
            edit: {
                featureGroup: drawnItems,
                remove: false,
                edit: false
            }
        };
        var drawControl = new L.Control.Draw(options);
        map.addControl(drawControl);
        map.on(L.Draw.Event.CREATED, function (e) {
            var type = e.layerType,
            layer = e.layer;
            editableLayers.addLayer(layer);
			//var upperright = e.layer._latlngs[0].lat + ";" + e.layer._latlngs[0].lng;
			//var upperleft = e.layer._latlngs[1].lat + ";" + e.layer._latlngs[1].lng;
			//var lowerleft = e.layer._latlngs[2].lat + ";" + e.layer._latlngs[2].lng;
			//var lowerright = e.layer._latlngs[3].lat + ";" + e.layer._latlngs[3].lng;
			console.log("latlng=" + e.layer._latlngs.lat + ";" + e.layer._latlngs.lng);
		});
        // load accordion
        $("#sidebar").accordion({collapsible: true, heightStyle: "content", autoHeight: false });
    }

    var loadProjects = function() {
        $.ajax({
            async: false,
            type: 'GET',
            url: projectsURL,
            error: function(jqXHR, textStatus, errorThrown) {
                console.info(textStatus);
            },
            success: function(response) {
                try {
                    response = JSON.parse(response);
                } catch (e) {}
                for (var project in response) {
                    $("#projects").append($("<option />").val(response[project].id).text(response[project].title));
                }
            }
        });
    }

    var loadPublisher = function() {
        $.ajax({
            async: false,
            type: 'GET',
            url: projectsURL,
            error: function(jqXHR, textStatus, errorThrown) {
                console.info(textStatus);
            },
            success: function(response) {
                try {
                    response = JSON.parse(response);
                } catch (e) {}
                for (var project in response) {
                    $("#creator").append($("<option />").val(response[project].creator).text(response[project].creator));
                }
            }
        });
    }

    var loadLanguages = function() {
        $.ajax({
            async: false,
            type: 'GET',
            url: searchURL,
            data: {"languages":true},
            error: function(jqXHR, textStatus, errorThrown) {
                console.info(textStatus);
            },
            success: function(response) {
                try {
                    response = JSON.parse(response);
                } catch (e) {}
                $("#langswitch").append($("<option />").val("").text("all languages"));
                for (var lang in response) {
                    $("#langswitch").append($("<option />").val(response[lang].value).text(response[lang].name));
                }
            }
        });
    }

    // init page
    init();

});
