


var destinationIterator = 0
var sellerId = window.location.href.substring(43);

var url = "/glimpse/destination/recommend/DAY_OUT";

var imageBaseUrl = "images/";

$.get(url, {sellerId:sellerId}, function(destinations){

console.log(destinations);
$.each(destinations, function(){

    $('body').append("<div class='order-item' id="+destinations[destinationIterator].destinationId+" ></div>");

    $('#'+destinations[destinationIterator].destinationId).append('<img src="'+imageBaseUrl+destinations[destinationIterator].images[0]+'" height="200"/>');

    if(destinations[destinationIterator].approvalState == null || destinations[destinationIterator].approvalState == "NOT_OPTED_IN"){
        $('#'+destinations[destinationIterator].destinationId).append("<div class='order-text'><div class='rhs-container'><div class='optin-btn-container'><input data='"+destinations[destinationIterator].destinationId+"' class='bidding-price' type='text' placeholder='Bidding Price' value=''/><input data='"+destinations[destinationIterator].destinationId+"' class='bidding-sla' type='text' placeholder='Bidding SLA' value=''/><input data='"+destinations[destinationIterator].destinationId+"' productId='"+destinations[destinationIterator].productId+"' listingLQS='"+destinations[destinationIterator].listingLQS+"'  productId='"+destinations[destinationIterator].destinationId+"' class='optin-btn' type='submit' value='Order OptIn'/></div></div></div>");
    }

     if(destinations[destinationIterator].approvalState == "OPTED_IN"){
            $('#'+destinations[destinationIterator].destinationId).append("<div class='order-text'><div class='rhs-container'><div class='optin-btn-container'>Bid Price: "+destinations[destinationIterator].bidOrderPrice+" <br />Bid SLA: "+destinations[destinationIterator].bidSla+"<br /><br /><input data='"+destinations[destinationIterator].destinationId+"' productId='"+destinations[destinationIterator].productId+"' listingLQS='"+destinations[destinationIterator].listingLQS+"' productId='"+destinations[destinationIterator].destinationId+"' class='optin-btn' type='submit' value='Opted In'/></div></div></div>");
     }

    if(destinations[destinationIterator].approvalState == "ORDER_PLACED"){
            $('#'+destinations[destinationIterator].destinationId).append("<div class='order-text'><div class='rhs-container'><div class='optin-btn-container'>Bid Price: "+destinations[destinationIterator].bidOrderPrice+" <br />Bid SLA: "+destinations[destinationIterator].bidSla+"<br /><br /><input data='"+destinations[destinationIterator].destinationId+"' productId='"+destinations[destinationIterator].productId+"' listingLQS='"+destinations[destinationIterator].listingLQS+"' productId='"+destinations[destinationIterator].destinationId+"' class='optin-btn' type='submit' value='Order Placed'/></div></div></div>");
     }

    $('#'+destinations[destinationIterator].destinationId).append("<span class='bold'>"+destinations[destinationIterator].name+"</span>");
        $('#'+destinations[destinationIterator].destinationId).append("<br />");

    $('#'+destinations[destinationIterator].destinationId).append("<span class='minimize'>destinationId: "+destinations[destinationIterator].id+"</span>");

    $('#'+destinations[destinationIterator].destinationId).append("<br />");

    $('#'+destinations[destinationIterator].destinationId).append("<span class='minimize'>listingId: "+destinations[destinationIterator].description+"</span>");

    $('#'+destinations[destinationIterator].destinationId).append("<br />");

    $('#'+destinations[destinationIterator].destinationId).append("<span class='minimize'>productId: "+destinations[destinationIterator].distance+"</span>");

    $('#'+destinations[destinationIterator].destinationId).append("<br />");

    $('#'+destinations[destinationIterator].destinationId).append("<span class='minimize'>Promised Price: "+destinations[destinationIterator].type+"</span>");

    $('#'+destinations[destinationIterator].destinationId).append("<br />");

    $('#'+destinations[destinationIterator].destinationId).append("<span class='minimize'>Promised SLA: "+destinations[destinationIterator].address.city+"</span>");

    $('#'+destinations[destinationIterator].destinationId).append("<br />");


    $('#'+destinations[destinationIterator].destinationId).append("<span class='minimize'>SellerId: "+destinations[destinationIterator].address.state+"</span>");

    $('#'+destinations[destinationIterator].destinationId).append("<br />");


    $('#'+destinations[destinationIterator].destinationId).append("<span class='minimize'>LQS: "+destinations[destinationIterator].listingLQS+"</span>");

    $('#'+destinations[destinationIterator].destinationId).append("<br />");

    destinationIterator++;

});

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
            "sellerId": sellerId,
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









