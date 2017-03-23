$(document).ready(function() {

    $('#b-labels').on('click', function() {
        $("#filter-lang-wrapper").show();
        $("#b-labels").removeClass("b-labels-notactive").addClass("b-labels-active");
        $("#b-objects").show();
        $("#b-projects").show();
        mode = "labels";
        getLabels();
    });

    $('#b-objects').on('click', function() {
        $("#filter-lang-wrapper").hide();
        $("#b-labels").removeClass("b-labels-notactive").addClass("b-labels-active");
        $("#b-objects").hide();
        $("#b-projects").show();
        mode = "objects";
        getDatasets();
    });

    $('#b-projects').on('click', function() {
        $("#filter-lang-wrapper").hide();
        $("#b-labels").removeClass("b-labels-notactive").addClass("b-labels-active");
        $("#b-objects").show();
        $("#b-projects").hide();
        mode = "projects";
        getProjects();
    });

    $('#b-timespan').on('click', function() {
        filter = {};
        setLanguageFilter();
        filter.start = $("#slider-time-range").slider("values", 0);
        filter.end = $("#slider-time-range").slider("values", 1);
        loadItems();
    });

    $('#b-project').on('click', function() {
        filter = {};
        setLanguageFilter();
        filter.project = $("#projects option:selected").val();
        loadItems();
    });

    $('#b-envelope').on('click', function() {
        setLanguageFilter();
        loadItems();
    });

    $('#b-creator').on('click', function() {
        filter = {};
        setLanguageFilter();
        filter.creator = $("#creator option:selected").val();
        loadItems();
    });

    $('#langswitch').on('change', function() {
        setLanguageFilter();
        loadItems();
    });

    var loadItems = function() {
        if (mode==="labels") {
            getLabels();
        } else if (mode==="objects") {
            getDatasets();
        } else {
            getProjects();
        }
    }

    var setLanguageFilter = function() {
        if ($("#langswitch").val().length !== 0) {
            filter.lang = $("#langswitch").val();
        } else {
            delete filter["lang"];
        }
    }

});
