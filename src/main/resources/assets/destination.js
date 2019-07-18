
var imageBaseUrl = "images/";

var getImageUrl = function(destination){
     return imageBaseUrl+destination.images[Math.floor(Math.random(0,destination.images.length) * destination.images.length)];
}

var loadDestination = function(destination){
    var url = "/glimpse/destination/"+destination;
    var imageBaseUrl = "images/";

    $.get(url, {destinationId:destinationId}, function(destination){

        console.log(destination);

        $('#main-content').html("<div class='destination-container' id="+destination.destinationId+" ></div>");
        if(destination.images != null && destination.images.length > 0){
            $('#'+destination.destinationId).append('<img class="destination-banner-image" src="'+getImageUrl(destination)+'"/>');
        }
        else{
           $('#'+destination.destinationId).append('<img class="destination-banner-image" src="site-images/glimpse-landing.jpeg"/>');
        }
        $('#'+destination.destinationId).append("<div class='destination-name'>"+destination.name+"</div>");
        $('#'+destination.destinationId).append("<div class='destination-tab'><div id='destination-tab-about' class='destination-tab-each'>About</div><div id='destination-tab-snaps' class='destination-tab-each'>Snaps</div>");
        $('#'+destination.destinationId).append("<div id='destination-details' class='destination-details'></div>");
        $('#destination-details').append("<div class='destination-description'>"+destination.description+"</div>");
        $('#'+destination.destinationId).append("<span class='minimize'>distance: "+destination.distance+"</span>");
        $('#'+destination.destinationId).append("<span class='minimize'>Type: "+destination.type+"</span>");
        $('#'+destination.destinationId).append("<span class='minimize'>City: "+destination.address.city+"</span>");
        $('#'+destination.destinationId).append("<span class='minimize'>State: "+destination.address.state+"</span>");

        $('#destination-tab-about').click(function(){
            console.log("about clicked");
            $('#destination-details').html("<div class='destination-description'>"+destination.description+"</div>");
        })

        $('#destination-tab-snaps').click(function(){
            console.log("snaps clicked");
            $('#destination-details').html("<table id='destination-snaps-table' class='destination-snaps-table'></table>");

            $.each(destination.images, function(index, image){
                if(index % 3 == 0) $('#destination-snaps-table').append("<tr class='destination-snap'></tr>");
                $('#destination-snaps-table').children().eq(parseInt(index/3)).append("<td><img class='destination-snap' src='"+imageBaseUrl+image+"'/></td>");
            });
        })

    $(".optin-btn").on("click",function(){
    console.log("clicking optin btn");
        var destinationId = $(this).attr("data");
        var productId = $(this).attr("productId");
        var listingLQS = $(this).attr("listingLQS");
        var me = $(this);
        var biddingPrice = me.parent().children().eq(0).val();
        var biddingSLA = me.parent().children().eq(1).val();

        if(biddingPrice > 0 && biddingSLA > 0 ){

            me.attr("value","OPTED_IN");


           var sellerRequest = {

                "destinationId": destinationId,
                "destinationId": destinationId,
                "productId": productId,
                "listingLQS": listingLQS,
                "bidOrderPrice" : biddingPrice,
                "bidSla": biddingSLA
           };


           var sellerRequest = JSON.stringify(sellerRequest);

            $.ajax({
                type: "POST",
                url: "/malgudi/seller/approvalInput",
                dataType: 'json',
                contentType:"application/json",
                data: sellerRequest,
                cache: false,
                success: function(data){
                    me.attr("value","OPTED_IN");
                }
            });
        }
        else{
            alert("Please enter a value for bidding price and bidding SLA");
        }
    });

    });
}


var destinationId = window.location.href.substring(43);

//var url = "/glimpse/destination/recommend/DAY_OUT";

var requestedWebPage = window.location.pathname.split("/")[1];

switch(requestedWebPage){
    case "recommend":
            console.log("recommend");
            break;
    case "index.html":
    case "":
    var destination = new URLSearchParams(window.location.search).get("destination");
        console.log("requested destination "+destination);
        loadDestination(destination);
        break;
    default:
        loadDestination("kanoor fort");
        console.log("destination page");
}
