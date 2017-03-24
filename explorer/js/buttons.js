$(document).ready(function() {

    $('#b-labels').on('click', function() {
        //$("#filter-lang-wrapper").show();
        $("#filter-lang-wrapper").removeClass("disablediv");
        $("#b-labels").removeClass("b-labels-notactive").addClass("b-labels-active");
        $("#b-objects").removeClass("b-objects-active").addClass("b-objects-notactive");
        $("#b-projects").removeClass("b-projects-active").addClass("b-projects-notactive");
        mode = "labels";
        getLabels();
    });

    $('#b-objects').on('click', function() {
        //$("#filter-lang-wrapper").show();
        $("#filter-lang-wrapper").addClass("disablediv");
        $("#b-labels").removeClass("b-labels-active").addClass("b-labels-notactive");
        $("#b-objects").removeClass("b-objects-notactive").addClass("b-objects-active");
        $("#b-projects").removeClass("b-projects-active").addClass("b-projects-notactive");
        mode = "objects";
        getDatasets();
    });

    $('#b-projects').on('click', function() {
        //$("#filter-lang-wrapper").show();
        $("#filter-lang-wrapper").addClass("disablediv");
        $("#b-labels").removeClass("b-labels-active").addClass("b-labels-notactive");
        $("#b-objects").removeClass("b-objects-active").addClass("b-objects-notactive");
        $("#b-projects").removeClass("b-projects-notactive").addClass("b-projects-active");
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
