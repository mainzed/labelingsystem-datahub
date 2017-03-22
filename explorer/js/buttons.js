$(document).ready(function() {

    $('#b-home').on('click', function() {
        loadLandingPage();
    });

    $('#b-butswitch').on('click', function() {
        if (mode==="labels") { // get objects
            $("#langswitch").hide();
            mode = "objects";
            getDatasets();
        } else if (mode==="objects") { // get labels
            $("#langswitch").show();
            mode = "labels";
            getLabels();
        }
    });

    $('#b-all').on('click', function() {
        filter = {};
        if (mode==="labels") {
            getLabels();
        } else {
            getDatasets();
        }
    });

    $('#b-timespan').on('click', function() {
        filter = {};
        filter.start = $("#slider-time-range").slider("values", 0);
        filter.end = $("#slider-time-range").slider("values", 1);
        if (mode==="labels") {
            getLabels();
        } else {
            getDatasets();
        }
    });

    $('#b-envelope').on('click', function() {
        filter = {};
        filter.lat_min = $("#lat_min").val();
        filter.lng_min = $("#lng_min").val();
        filter.lat_max = $("#lat_max").val();
        filter.lng_max = $("#lng_max").val();
        if (mode==="labels") {
            getLabels();
        } else {
            getDatasets();
        }
    });

    $('#b-project').on('click', function() {
        filter = {};
        filter.project = $("#projects option:selected").val();
        if (mode==="labels") {
            getLabels();
        } else {
            getDatasets();
        }
    });

    $('#b-creator').on('click', function() {
        filter = {};
        filter.creator = $("#creator option:selected").val();
        if (mode==="labels") {
            getLabels();
        } else {
            getDatasets();
        }
    });

    // on language change
    $('#langswitch').on('change', function() {
        if ($("#langswitch").val().length !== 0) {
            filter.lang = $("#langswitch").val();
        } else {
            delete filter["lang"];
        }
        if (mode==="labels") {
            getLabels();
        } else {
            getDatasets();
        }
    });
});
