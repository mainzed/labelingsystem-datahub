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
    var div = "<p>Labeling System+ Lucy Edition</p>";
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
