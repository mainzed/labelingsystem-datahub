$(document).ready(function() {

    var init = function() {
        loadProjects();
        loadPublisher();
        loadLanguages();
        getLabels();
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
        map = L.map('map').setView([41.373793, 45.311555], 2);
        L.tileLayer('http://{s}.tiles.mapbox.com/v3/isawnyu.map-knmctlkh/{z}/{x}/{y}.png', {
			maxZoom: 15
        }).addTo(map);
        // set points
        $.ajax({
            async: false,
            type: 'GET',
            url: searchURL,
            data: {"geojson":true},
            error: function(jqXHR, textStatus, errorThrown) {
                console.info(textStatus);
            },
            success: function(response) {
                try {
                    response = JSON.parse(response);
                } catch (e) {}

                var geojsonMarkerOptions = {
                    radius: 6,
                    fillColor: "#ff7800",
                    color: "#000000",
                    weight: 1,
                    opacity: 1,
                    fillOpacity: 1
                };
                L.geoJSON(response, {
                    pointToLayer: function (feature, latlng) {
                        return L.circleMarker(latlng, geojsonMarkerOptions);
                    }
                }).addTo(map);
            }
        });
        // set attribution
        $("#map-attribution").html("map attribution: "+"Tiles &copy; <a href='http://mapbox.com/' target='_blank'>MapBox</a> | Data &copy; <a href='http://www.openstreetmap.org/' target='_blank'>OpenStreetMap</a> and contributors, CC-BY-SA | Tiles and Data &copy; 2013 <a href='http://www.awmc.unc.edu' target='_blank'>AWMC</a><a href='http://creativecommons.org/licenses/by-nc/3.0/deed.en_US' target='_blank'>CC-BY-NC 3.0</a>")
        // set leaflet draw
        drawnItems = new L.FeatureGroup();
        editableLayers = new L.FeatureGroup();
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
            editableLayers.addLayer(e.layer);
            getEnvelope(e.layer._latlngs[0][0].lat, e.layer._latlngs[0][0].lng, e.layer._latlngs[0][1].lat, e.layer._latlngs[0][2].lng);
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

    var getEnvelope = function(lat_min, lng_min, lat_max, lng_max) {
        filter = {};
        filter.lat_min = lat_min;
        filter.lng_min = lng_min;
        filter.lat_max = lat_max;
        filter.lng_max = lng_max;
        console.log(filter);
        $('#b-envelope').click();
    }

});
