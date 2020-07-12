type = ['primary', 'info', 'success', 'warning', 'danger'];
var moteName;
var chart_labels = [];
var chart_data = [];

pageChart = {
  initDashboardPageCharts: function() {
    $("#chart-container").empty();
    $("#chart-container").append('<canvas id="chartBig1"></canvas>');
    
    gradientChartOptionsConfigurationWithTooltipBlue = {
      maintainAspectRatio: false,
      legend: {
        display: false
      },

      tooltips: {
        backgroundColor: '#f5f5f5',
        titleFontColor: '#333',
        bodyFontColor: '#666',
        bodySpacing: 4,
        xPadding: 12,
        mode: "nearest",
        intersect: 0,
        position: "nearest"
      },
      responsive: true,
      scales: {
        yAxes: [{
          barPercentage: 1.6,
          gridLines: {
            drawBorder: false,
            color: 'rgba(29,140,248,0.0)',
            zeroLineColor: "transparent",
          },
          ticks: {
            suggestedMin: 60,
            suggestedMax: 125,
            padding: 20,
            fontColor: "#2380f7"
          }
        }],

        xAxes: [{
          barPercentage: 1.6,
          gridLines: {
            drawBorder: false,
            color: 'rgba(29,140,248,0.1)',
            zeroLineColor: "transparent",
          },
          ticks: {
            padding: 20,
            fontColor: "#2380f7"
          }
        }]
      }
    };

    gradientChartOptionsConfigurationWithTooltipPurple = {
      maintainAspectRatio: false,
      legend: {
        display: false
      },

      tooltips: {
        backgroundColor: '#f5f5f5',
        titleFontColor: '#333',
        bodyFontColor: '#666',
        bodySpacing: 4,
        xPadding: 12,
        mode: "nearest",
        intersect: 0,
        position: "nearest"
      },
      responsive: true,
      scales: {
        yAxes: [{
          barPercentage: 1.6,
          gridLines: {
            drawBorder: false,
            color: 'rgba(29,140,248,0.0)',
            zeroLineColor: "transparent",
          },
          ticks: {
            suggestedMin: 60,
            suggestedMax: 125,
            padding: 20,
            fontColor: "#9a9a9a"
          }
        }],

        xAxes: [{
          barPercentage: 1.6,
          gridLines: {
            drawBorder: false,
            color: 'rgba(225,78,202,0.1)',
            zeroLineColor: "transparent",
          },
          ticks: {
            padding: 20,
            fontColor: "#9a9a9a"
          }
        }]
      }
    };

    gradientChartOptionsConfigurationWithTooltipOrange = {
      maintainAspectRatio: false,
      legend: {
        display: false
      },

      tooltips: {
        backgroundColor: '#f5f5f5',
        titleFontColor: '#333',
        bodyFontColor: '#666',
        bodySpacing: 4,
        xPadding: 12,
        mode: "nearest",
        intersect: 0,
        position: "nearest"
      },
      responsive: true,
      scales: {
        yAxes: [{
          barPercentage: 1.6,
          gridLines: {
            drawBorder: false,
            color: 'rgba(29,140,248,0.0)',
            zeroLineColor: "transparent",
          },
          ticks: {
            suggestedMin: 50,
            suggestedMax: 110,
            padding: 20,
            fontColor: "#ff8a76"
          }
        }],

        xAxes: [{
          barPercentage: 1.6,
          gridLines: {
            drawBorder: false,
            color: 'rgba(220,53,69,0.1)',
            zeroLineColor: "transparent",
          },
          ticks: {
            padding: 20,
            fontColor: "#ff8a76"
          }
        }]
      }
    };

    gradientChartOptionsConfigurationWithTooltipGreen = {
      maintainAspectRatio: false,
      legend: {
        display: false
      },

      tooltips: {
        backgroundColor: '#f5f5f5',
        titleFontColor: '#333',
        bodyFontColor: '#666',
        bodySpacing: 4,
        xPadding: 12,
        mode: "nearest",
        intersect: 0,
        position: "nearest"
      },
      responsive: true,
      scales: {
        yAxes: [{
          barPercentage: 1.6,
          gridLines: {
            drawBorder: false,
            color: 'rgba(29,140,248,0.0)',
            zeroLineColor: "transparent",
          },
          ticks: {
            suggestedMin: 50,
            suggestedMax: 125,
            padding: 20,
            fontColor: "#9e9e9e"
          }
        }],

        xAxes: [{
          barPercentage: 1.6,
          gridLines: {
            drawBorder: false,
            color: 'rgba(0,242,195,0.1)',
            zeroLineColor: "transparent",
          },
          ticks: {
            padding: 20,
            fontColor: "#9e9e9e"
          }
        }]
      }
    };


    gradientBarChartConfiguration = {
      maintainAspectRatio: false,
      legend: {
        display: false
      },

      tooltips: {
        backgroundColor: '#f5f5f5',
        titleFontColor: '#333',
        bodyFontColor: '#666',
        bodySpacing: 4,
        xPadding: 12,
        mode: "nearest",
        intersect: 0,
        position: "nearest"
      },
      responsive: true,
      scales: {
        yAxes: [{

          gridLines: {
            drawBorder: false,
            color: 'rgba(29,140,248,0.1)',
            zeroLineColor: "transparent",
          },
          ticks: {
            suggestedMin: 60,
            suggestedMax: 120,
            padding: 20,
            fontColor: "#9e9e9e"
          }
        }],

        xAxes: [{

          gridLines: {
            drawBorder: false,
            color: 'rgba(29,140,248,0.1)',
            zeroLineColor: "transparent",
          },
          ticks: {
            padding: 20,
            fontColor: "#9e9e9e"
          }
        }]
      }
    };

    var ctx = document.getElementById("chartBig1").getContext('2d');

    var gradientStroke = ctx.createLinearGradient(0, 230, 0, 50);

    gradientStroke.addColorStop(1, 'rgba(72,72,176,0.1)');
    gradientStroke.addColorStop(0.4, 'rgba(72,72,176,0.0)');
    gradientStroke.addColorStop(0, 'rgba(119,52,169,0)'); //purple colors
    var config = {
      type: 'line',
      data: {
        labels: chart_labels,
        datasets: [{
          label: "",
          fill: true,
          backgroundColor: gradientStroke,
          borderColor: '#d346b1',
          borderWidth: 2,
          borderDash: [],
          borderDashOffset: 0.0,
          pointBackgroundColor: '#d346b1',
          pointBorderColor: 'rgba(255,255,255,0)',
          pointHoverBackgroundColor: '#d346b1',
          pointBorderWidth: 20,
          pointHoverRadius: 4,
          pointHoverBorderWidth: 15,
          pointRadius: 4,
          data: chart_data,
        }]
      },
      options: gradientChartOptionsConfigurationWithTooltipPurple
    };
    var myChartData = new Chart(ctx, config);
    


  },
};


function populateSensors(){
	$.ajax({
            'url' : 'sensors?action=list&type=sensor',
            'type' : 'GET',
            'data' : null,
            'cache': false,
            'contentType': false,
            'processData': false,
            'success' : function(data) {    
          		var tableSensors = $("#body-table-sensors");
      				tableSensors.empty();
      				console.log(data);
      				var sensors = data["array"];
      				sensors.forEach(function(sensor){
      				tableSensors.append($("<tr onclick='selectSensor(this, \"" + sensor['name'] + "\")' ><td><input type='text' class='form-control text-center' value='" + sensor['name'] + "' onchange='updateName(this, \"" + sensor['name'] + "\")'></td><td>"+sensor['resource']+"</td><td>"+((sensor['assigned'] != null)?sensor['assigned']:'')+"</td></tr>"));
      				});

      				populateActuators(sensors);
            }
    });
	
}


function populateActuators(sensors) {
	$.ajax({
            'url' : 'sensors?action=list&type=actuator',
            'type' : 'GET',
            'data' : null,
            'cache': false,
            'contentType': false,
            'processData': false,
            'success' : function(data) {    
        		var tableActuators = $("#body-table-actuators");
				tableActuators.empty()
        console.log(data);
				var actuators = data["array"];
				actuators.forEach(function (actuator) {
					var newRow = "<tr><td><input type='text' class='form-control text-center' value='" + actuator['name'] + "' onchange='updateName(this, \"" + actuator['name'] + "\")'></td><td>" + actuator['resource'] + "</td><td><select class='form-control' onchange='updateAssignedSensor(this,\""+actuator['name']+"\")'>";
					if(actuator["assigned"] == null){
						newRow += "<option value='None' selected>None</option>";
					}
          else{
            newRow += "<option value='None'>None</option>";
          }
					sensors.forEach(function(sensor){
					if(sensor["resource"] == "temperature"){
						if (actuator["assigned"] == sensor["name"]) {
							newRow += "<option value='" + sensor['name'] + "' selected>"+sensor['name']+"</option>";
						} else {
							newRow += "<option value='" + sensor['name'] + "'>"+sensor['name']+"</option>";
						}
					}
					})
          newRow += "</select></td>"
          if(actuator["assigned"] == null){
            newRow += "<td><button class='btn btn-success' onclick='updateActuatorValue(\""  + actuator['name'] + "\")'>"+(actuator['value'] == 1?'Close':'Open')+"</button></td></tr>";
          }
          else{
            newRow += "<td><button class='btn btn-success' disabled>"+(actuator['value'] == 0?'Closed':'Opened')+"</button></td></tr>";
          }
          console.log(newRow);
					tableActuators.append(newRow);
				});
            }
    });
}


var selectedRow = null;

function updateName(input, oldName){
  var newName = $(input).val();
  console.log("Prova")
  $.ajax({
            'url' : 'sensors?action=updateMote&oldName=' + oldName + '&newName=' + newName,
            'type' : 'PUT',
            'data' : null,
            'cache': false,
            'contentType': false,
            'processData': false,
            'success' : function(data) {
              location.reload();
            }
    });
  console.log(oldName);
  console.log(newName);
}

function updateAssignedSensor(input, actuatorName){
  var selectedSensor = $(input).val();
  $.ajax({
            'url' : 'sensors?action=assignActuator&moteName=' + selectedSensor + '&actuatorName=' + actuatorName,
            'type' : 'PUT',
            'data' : null,
            'cache': false,
            'contentType': false,
            'processData': false,
            'success' : function(data) {
              location.reload();
            }
    });
  console.log(selectedSensor);
  console.log(actuatorName);
  //populateSensors();
}

function updateActuatorValue(actuatorName){
  $.ajax({
            'url' : 'sensors?action=actuatorValue&actuatorName=' + actuatorName,
            'type' : 'PUT',
            'data' : null,
            'cache': false,
            'contentType': false,
            'processData': false,
            'success' : function(data) {
              location.reload();
            }
    });
}

function selectSensor(row, name){
  if(selectedRow != null)
    $(selectedRow).removeClass("active");

  $(row).addClass("active");
  selectedRow = row;
  moteName = name;
  $("#chart-title").text(name);
  console.log(moteName)
  $.ajax({
            'url' : 'sensors?action=values&name=' + moteName,
            'type' : 'GET',
            'data' : null,
            'cache': false,
            'contentType': false,
            'processData': false,
            'success' : function(data) {
      				chart_labels = data["labels"];
      				chart_data = data["values"];
      				pageChart.initDashboardPageCharts()
            }
    });
}


populateSensors();
