var loadLandingPage = function() {
    $("#content").empty();
    var div = "<div id='landingpage'>";
    div += "<p>This is the landing page.</p>";
    div += "</div>";
    $("#content").append(div);
}

$(document).ready(function() {

    var init = function() {
        $("#b-butswitch").html("datasets view");
        loadProjects();
        loadPublisher();
        loadLanguages();
        loadLandingPage();
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
                    $("#publisher").append($("<option />").val(response[project].publisher).text(response[project].creator));
                }
            }
        });
    }

    var loadLanguages = function() {
        $.ajax({
            async: false,
            type: 'GET',
            url: searchURL,
            data: {"labels":true},
            error: function(jqXHR, textStatus, errorThrown) {
                console.info(textStatus);
            },
            success: function(response) {
                try {
                    response = JSON.parse(response);
                } catch (e) {}
                for (var label in response) {
                    $("#langswitch").append($("<option />").val(response[label].lang).text(response[label].lang));
                }
            }
        });
    }

    // init page
    init();

});
