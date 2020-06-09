
var map;
function initMap() {
    var mapOptions = {
    center: {lat: 41, lng: -87},
    zoom: 4
    };

    map = new google.maps.Map(document.getElementById('map'), mapOptions);

fetch('/superfund').then(response => response.json()).then((sites) => {
    sites.forEach((site) => {
      new google.maps.Marker(
          {position: {lat: site.lattitude, lng: site.longitude}, 
          title: site.name,
          map: map});
    });
  });
}

