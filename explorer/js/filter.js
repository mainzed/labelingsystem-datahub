$(document).ready(function() {
    $('#filterheader').on('click', function() {
        // reset filters
        filter = {};
        // setLanguageFilter
        if ($("#langswitch").val().length !== 0) {
            filter.lang = $("#langswitch").val();
        } else {
            delete filter["lang"];
        }
        // load items
        if (mode==="labels") {
            getLabels();
        } else if (mode==="objects") {
            getDatasets();
        } else {
            getProjects();
        }
        // selected
        $("#filter-project").show();
        $("#filter-creator").show();
        $("#filter-timespan").show();
        $("#filter-envelope").show();
        // properties
        $("#filter-properties-wrapper").hide();
        $("#filter-project-properties").hide();
        $("#filter-creator-properties").hide();
        $("#filter-timespan-properties").hide();
        $("#filter-envelope-properties").hide();
        // label selection?
        if (mode==="labels") {
            $("#filter-lang-wrapper").show();
        } else {
            $("#filter-lang-wrapper").hide();
        }
    });
    $('#filter-project').on('click', function() {
        // clear map
        clearMap();
        // selected
        $("#filter-project").show();
        $("#filter-creator").hide();
        $("#filter-timespan").hide();
        $("#filter-envelope").hide();
        // properties
        $("#filter-properties-wrapper").show();
        $("#filter-project-properties").show();
        $("#filter-creator-properties").hide();
        $("#filter-timespan-properties").hide();
        $("#filter-envelope-properties").hide();
        $("#filterheader-properties").html("filter properties");
        // label selection?
        if (mode==="labels") {
            $("#filter-lang-wrapper").show();
        } else {
            $("#filter-lang-wrapper").hide();
        }
    });
    $('#filter-creator').on('click', function() {
        // clear map
        clearMap();
        // selected
        $("#filter-project").hide();
        $("#filter-creator").show();
        $("#filter-timespan").hide();
        $("#filter-envelope").hide();
        // properties
        $("#filter-properties-wrapper").show();
        $("#filter-project-properties").hide();
        $("#filter-creator-properties").show();
        $("#filter-timespan-properties").hide();
        $("#filter-envelope-properties").hide();
        $("#filterheader-properties").html("filter properties");
        // label selection?
        if (mode==="labels") {
            $("#filter-lang-wrapper").show();
        } else {
            $("#filter-lang-wrapper").hide();
        }
    });
    $('#filter-timespan').on('click', function() {
        // clear map
        clearMap();
        // selected
        $("#filter-project").hide();
        $("#filter-creator").hide();
        $("#filter-timespan").show();
        $("#filter-envelope").hide();
        // properties
        $("#filter-properties-wrapper").show();
        $("#filter-project-properties").hide();
        $("#filter-creator-properties").hide();
        $("#filter-timespan-properties").show();
        $("#filter-envelope-properties").hide();
        $("#filterheader-properties").html("filter properties");
        // label selection?
        if (mode==="labels") {
            $("#filter-lang-wrapper").show();
        } else {
            $("#filter-lang-wrapper").hide();
        }
    });
    $('#filter-envelope').on('click', function() {
        // rezize map
        map._onResize();
        envelope = true;
        // selected
        $("#filter-project").hide();
        $("#filter-creator").hide();
        $("#filter-timespan").hide();
        $("#filter-envelope").show();
        // properties
        $("#filter-properties-wrapper").show();
        $("#filter-project-properties").hide();
        $("#filter-creator-properties").hide();
        $("#filter-timespan-properties").hide();
        $("#filter-envelope-properties").show();
        $("#filterheader-properties").html("filter properties");
        // label selection?
        if (mode==="labels") {
            $("#filter-lang-wrapper").show();
        } else {
            $("#filter-lang-wrapper").hide();
        }
    });
    var clearMap = function() {
        map.removeLayer(drawnItems);
        map.removeLayer(editableLayers);
        drawnItems = new L.FeatureGroup();
        editableLayers = new L.FeatureGroup();
        map.addLayer(editableLayers);
        map.addLayer(drawnItems);
        envelope = false;
    }
});
