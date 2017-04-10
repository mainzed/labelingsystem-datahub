$(document).ready(function() {

    $('#b-home').on('click', function() {
        location.reload();
    });

    $('#b-labels').on('click', function() {
        $("#filter-lang-wrapper").removeClass("disablediv");
        $("#b-labels").addClass("disablediv");
        $("#b-objects").removeClass("disablediv");
        $("#b-projects").removeClass("disablediv");
        $("#b-resources").removeClass("disablediv");
        $("#filter-project").removeClass("disablediv");
        $("#filter-creator").removeClass("disablediv");
        $("#filter-timespan").removeClass("disablediv");
        $("#filter-envelope").removeClass("disablediv");
        $("#filter-resourcetype").addClass("disablediv");
        mode = "labels";
        $("#contentcontent").empty();
        $("#searchString").val("").focus();
        var target = document.getElementById('contentcontent');
        var spinner = new Spinner(optsSpin).spin(target);
        getLabels();
    });

    $('#b-objects').on('click', function() {
        $("#filter-lang-wrapper").addClass("disablediv");
        $("#b-labels").removeClass("disablediv");
        $("#b-objects").addClass("disablediv");
        $("#b-projects").removeClass("disablediv");
        $("#b-resources").removeClass("disablediv");
        $("#filter-project").removeClass("disablediv");
        $("#filter-creator").removeClass("disablediv");
        $("#filter-timespan").removeClass("disablediv");
        $("#filter-envelope").removeClass("disablediv");
        $("#filter-resourcetype").addClass("disablediv");
        mode = "objects";
        $("#contentcontent").empty();
        $("#searchString").val("").focus();
        var target = document.getElementById('contentcontent');
        var spinner = new Spinner(optsSpin).spin(target);
        getDatasets();
    });

    $('#b-projects').on('click', function() {
        $("#filter-lang-wrapper").addClass("disablediv");
        $("#b-labels").removeClass("disablediv");
        $("#b-objects").removeClass("disablediv");
        $("#b-projects").addClass("disablediv");
        $("#b-resources").removeClass("disablediv");
        $("#filter-project").removeClass("disablediv");
        $("#filter-creator").removeClass("disablediv");
        $("#filter-timespan").removeClass("disablediv");
        $("#filter-envelope").removeClass("disablediv");
        $("#filter-resourcetype").addClass("disablediv");
        mode = "projects";
        $("#contentcontent").empty();
        $("#searchString").val("").focus();
        var target = document.getElementById('contentcontent');
        var spinner = new Spinner(optsSpin).spin(target);
        getProjects();
    });

    $('#b-resources').on('click', function() {
        $("#filter-lang-wrapper").addClass("disablediv");
        $("#b-labels").removeClass("disablediv");
        $("#b-objects").removeClass("disablediv");
        $("#b-projects").removeClass("disablediv");
        $("#b-resources").addClass("disablediv");
        $("#filter-project").addClass("disablediv");
        $("#filter-creator").addClass("disablediv");
        $("#filter-timespan").addClass("disablediv");
        $("#filter-envelope").addClass("disablediv");
        $("#filter-resourcetype").removeClass("disablediv");
        mode = "resources";
        $("#contentcontent").empty();
        $("#searchString").val("").focus();
        var target = document.getElementById('contentcontent');
        var spinner = new Spinner(optsSpin).spin(target);
        getResources();
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

    $('#b-resourcetype').on('click', function() {
        loadItems();
        /*
        var seltype = $("#resourcetype option:selected").val();
        var displayedResources = [];
        if (seltype!=="") {
            for (var item in resources) {
                if (resources[item].type===seltype) {
                    displayedResources.push(resources[item]);
                }
            }
        } else {
            displayedResources = resources;
        }
        // sort array
        displayedResourcesCopy = sortArrayByValue(displayedResources,"label","uri");
        // create divs
        $("#contentcontent").empty();
        for (var obj in displayedResourcesCopy) {
            var div = "<div id='"+displayedResourcesCopy[obj].uri+"' class='resource'>";
            var sublabel = displayedResourcesCopy[obj].label.substring(0,20);
            if (displayedResourcesCopy[obj].label.length > 20) {
                sublabel += " [...]";
            }
            div += "<div class='resourcevalue'>"+sublabel+"</div>";
            div += "<div class='resourcetype'>";
            div += "type: "+displayedResourcesCopy[obj].type;
            div += "</div>";
            div += "<div class='resourceproperties'>";
            var subdescription = displayedResourcesCopy[obj].description.substring(0,200);
            if (displayedResourcesCopy[obj].description.length > 200) {
                subdescription += " [...]";
            }
            div += "description: "+subdescription;
            div += "</div>";
            div += "</div>";
            $("#contentcontent").append(div);
        }
        // init nanoscroller
        $(".nano").nanoScroller();*/
    });

    var loadItems = function() {
        if (mode==="labels") {
            getLabels();
        } else if (mode==="objects") {
            getDatasets();
        } else if (mode==="projects") {
            getProjects();
        } else if (mode==="resources") {
            getResources();
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
