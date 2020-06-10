
var map;
function initMap() {
    var mapOptions = {
    center: {lat: 42, lng: -9},
    zoom: 4
    };

    map = new google.maps.Map(document.getElementById('map'), mapOptions);

fetch('/superfund').then(response => response.json()).then((sites) => {
    sites.forEach((site) => {
      var marker = new google.maps.Marker(
				{position: {lat: site.lattitude, lng: site.longitude}, 
				title: site.name,
				icon: iconUrl(site.score),
				map: map});
			var contentStr = "<h3>"+site.name+"</h3>" +
				"<h4>"+site.city+", "+site.state+"</h4>"+
				"<h4>Hazard Rating of: "+site.score+"</h4>";
      var infoWindow = new google.maps.InfoWindow({
				content: contentStr
			});
    	marker.addListener('click', () => {
				infoWindow.open(map, marker);
			});
			map.addListener('click', () => {
				infoWindow.close();
			})
    });
  });
}

function iconUrl(score){
	var url = "https://maps.google.com/mapfiles/ms/icons/";
	if(score < 25) url+= "green";
	else if(score <40) url += "yellow";
	else if (score < 55) url +="orange";
	else url +="red";
	url += "-dot.png";
	return url;
}

