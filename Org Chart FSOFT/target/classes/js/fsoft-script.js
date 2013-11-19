(function($){
    $(document).ready(function(){
        if (AJS) {
            AJS.$('#fsoft-btn-sync').on('click', function() {
                var syncRequest = AJS.$.ajax({
                    url: "/rest/sync-role-fsoft/1.0/fsoft-sync-roles-service/syncTempoProject",
                    type: "POST",
                    data: JSON.stringify({projectId: 1, username: "datlt2"}),
                    contentType: "application/json",
                    dataType: "json",
                    success: function(data, status, xhr) {
                        console.log(data);
                        alert("Sync succeeded.");
                    },
                    error: function(xhr, status, msg) {
                        alert("Failed to sync between tempo and project.");
                    }
                });
            });
        } else {
            alert("AJS is undefined!!!");
        }
    });

})(AJS.$);