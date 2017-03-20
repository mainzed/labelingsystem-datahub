$(document).ready(function() {

    $('#b-home').on('click', function() {
        loadLandingPage();
    });

    $('#b-butswitch').on('click', function() {
        if (mode==="labels") { // get objects
            mode = "objects";
            getDatasets();
        } else if (mode==="objects") { // get labels
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
        filter.start = $("#start").val();
        filter.end = $("#end").val();
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

    $('#b-publisher').on('click', function() {
        filter = {};
        filter.project = $("#publisher option:selected").val();
        if (mode==="labels") {
            getLabels();
        } else {
            getDatasets();
        }
    });

    // fill language dropdown
    $('#langswitch').on('change', function() {
        filter.lang = $("#langswitch").val();
        if (mode==="labels") {
            getLabels();
        } else {
            getDatasets();
        }
    });
});
