$(document).ready(function() {
    $('#filterheader').on('click', function() {
        // filterheader
        $("#filterheader").html('<img src="img/icon-funnel.png" style="height:25px">');
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
        $("#filter-project").removeClass("disablediv");
        $("#filter-creator").removeClass("disablediv");
        $("#filter-timespan").removeClass("disablediv");
        $("#filter-envelope").removeClass("disablediv");
        // properties
        $("#filter-properties-wrapper").hide();
        $("#filter-project-properties").hide();
        $("#filter-creator-properties").hide();
        $("#filter-timespan-properties").hide();
        $("#filter-envelope-properties").hide();
    });
    $('#filter-project').on('click', function() {
        // filterheader
        $("#filterheader").html('<img src="img/icon-funnel-del.png" style="height:25px">');
        // clear map
        clearMap();
        // selected
        $("#filter-project").removeClass("disablediv");
        $("#filter-creator").addClass("disablediv");
        $("#filter-timespan").addClass("disablediv");
        $("#filter-envelope").addClass("disablediv");
        // properties
        $("#filter-properties-wrapper").show();
        $("#filter-project-properties").show();
        $("#filter-creator-properties").hide();
        $("#filter-timespan-properties").hide();
        $("#filter-envelope-properties").hide();
        $("#filterheader-properties").html("filter properties");
    });
    $('#filter-creator').on('click', function() {
        // filterheader
        $("#filterheader").html('<img src="img/icon-funnel-del.png" style="height:25px">');
        // clear map
        clearMap();
        // selected
        $("#filter-project").addClass("disablediv");
        $("#filter-creator").removeClass("disablediv");
        $("#filter-timespan").addClass("disablediv");
        $("#filter-envelope").addClass("disablediv");
        // properties
        $("#filter-properties-wrapper").show();
        $("#filter-project-properties").hide();
        $("#filter-creator-properties").show();
        $("#filter-timespan-properties").hide();
        $("#filter-envelope-properties").hide();
        $("#filterheader-properties").html("filter properties");
    });
    $('#filter-timespan').on('click', function() {
        // filterheader
        $("#filterheader").html('<img src="img/icon-funnel-del.png" style="height:25px">');
        // clear map
        clearMap();
        // selected
        $("#filter-project").addClass("disablediv");
        $("#filter-creator").addClass("disablediv");
        $("#filter-timespan").removeClass("disablediv");
        $("#filter-envelope").addClass("disablediv");
        // properties
        $("#filter-properties-wrapper").show();
        $("#filter-project-properties").hide();
        $("#filter-creator-properties").hide();
        $("#filter-timespan-properties").show();
        $("#filter-envelope-properties").hide();
        $("#filterheader-properties").html("filter properties");
    });
    $('#filter-envelope').on('click', function() {
        // filterheader
        $("#filterheader").html('<img src="img/icon-funnel-del.png" style="height:25px">');
        // rezize map
        map._onResize();
        envelope = true;
        // selected
        $("#filter-project").addClass("disablediv");
        $("#filter-creator").addClass("disablediv");
        $("#filter-timespan").addClass("disablediv");
        $("#filter-envelope").removeClass("disablediv");
        // properties
        $("#filter-properties-wrapper").show();
        $("#filter-project-properties").hide();
        $("#filter-creator-properties").hide();
        $("#filter-timespan-properties").hide();
        $("#filter-envelope-properties").show();
        $("#filterheader-properties").html("filter properties");
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
