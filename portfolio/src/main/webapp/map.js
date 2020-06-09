
var map;
function initMap() {
    var mapOptions = {
    center: {lat: 41, lng: -87},
    zoom: 4
    };

    map = new google.maps.Map(document.getElementById('map'), mapOptions);

fetch('/superfund').then(response => response.json()).then((sites) => {
    sites.forEach((site) => {
      var marker = new google.maps.Marker(
				{position: {lat: site.lattitude, lng: site.longitude}, 
				title: site.name,
				map: map});
			var contentStr = "<h3>"+site.name+"</h3>" +
				"<h4>Hazard Rating of: "+site.score+"</h4>";
      var infoWindow = new google.maps.InfoWindow({
				content: contentStr
			})
    	marker.addListener('click', () => {
				infoWindow.open(map, marker);
			});
			map.addListener('click', () => {
				infoWindow.close();
			})
    });
  });
}

