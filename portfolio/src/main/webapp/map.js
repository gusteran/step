google.charts.load('current', {packages: ['corechart']});
google.charts.setOnLoadCallback(drawSuperfundChart);

var map;
var lastInfoWindow = null;

function loadData(){
    initMap();

    fetch('/water').then(response => response.json()).then((water_systems) => {
        addCircles(water_systems);
        (water_systems);
    });

    fetch('/water?statistics=full').then(response => response.json()).then((water_systems) => {
        drawWaterChart(water_systems);
    });

  fetch('/superfund').then(response => response.json()).then((sites) => {
      addMarkers(sites);
        drawSuperfundChart(sites);
  });
}

function initMap(sites) {
    var mapOptions = {
    center: {lat: 42, lng: -93},
    zoom: 4,
    gestureHandling: 'greedy'
    };

    map = new google.maps.Map(document.getElementById('map'), mapOptions);
}

function addMarkers(sites){
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
                if(lastInfoWindow != null) {lastInfoWindow.close();}
				infoWindow.open(map, marker);
                lastInfoWindow = infoWindow;
			});
			map.addListener('click', () => {
				infoWindow.close();
			});
    });
    console.log("Placed Superfund Markers");
}

function addCircles(water_systems) {

    water_systems.forEach((system) => {
        var radius = Math.sqrt(system.population+2) * 20;
        if(system.population < 5000){
            radius = 5000;
        }

        var cityCircle = new google.maps.Circle({
            strokeColor: 'blue',
            strokeOpacity: 0.8,
            strokeWeight: 2,
            fillColor: 'blue',
            fillOpacity: 0.35,
            map: map,
            center: {
            lat: system.lat, 
            lng: system.lng},
            radius: radius
          });
          var contentStr = "<h3>"+system.name+"</h3>"+
            "<h4>Population Served: "+system.population+"</h4>"+
            "<h4>Address: "+system.address+"</h4>"+
            "<h4>Violations: "+system.violations+"</h4>";
        var infoWindow = new google.maps.InfoWindow({
				content: contentStr,
                position: {
                    lat: system.lat, 
                    lng: system.lng}
			});
    	cityCircle.addListener('click', () => {
                if(lastInfoWindow != null) {lastInfoWindow.close();}
				infoWindow.open(map);
                lastInfoWindow = infoWindow;
			});
			map.addListener('click', () => {
				infoWindow.close();
			});
    });
    console.log("Water Districts Placed");
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

function drawSuperfundChart(sites){
    
    var data = new google.visualization.DataTable();
      data.addColumn('string', 'Name');
      data.addColumn('number', 'Hazard Score');

    sites.forEach((site) => {
        data.addRow([site.name, site.score]);});

        var options = {
            title: 'Superfund Hazard Scores',
            label: 'Superfund Sites',
            legend: { position: 'none' },
            colors: ['green', 'yellow','orange','red'],
        };
      var chart = new google.visualization.Histogram(document.getElementById('myPieChart'));
      chart.draw(data, options);

}

function drawWaterChart(violations){
    
    var data = new google.visualization.DataTable();
      data.addColumn('string', 'Contaminant');
      data.addColumn('number', 'People Exposed');

    for(var violation in violations) {
        data.addRow([violation, violations[violation]]);
    }

        var options = {
            title: 'Population Exposed to Certain Water Pollutants',
            legend: { position: 'none' },
            bar: {groupWidth: "50%"},
            height: 700
        };
      var chart = new google.visualization.BarChart(document.getElementById('barGraph'));
      chart.draw(data, options);

}

