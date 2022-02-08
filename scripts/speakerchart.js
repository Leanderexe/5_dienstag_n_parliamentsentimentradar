var ctx2=document.getElementById("speakerchart");
var myChart = new Chart(ctx2, {
     type: 'bar',
     data: {
         labels: ['Red', 'Blue', 'Yellow', 'Green', 'Purple', 'Orange'],
         datasets: [{
             label: '# of Votes',
             data: [12, 19, 3, 5, 3, 3],
             backgroundColor: [
                 'rgba(255, 99, 132, 0.2)',
                 'rgba(54, 162, 235, 0.2)',
                 'rgba(255, 206, 86, 0.2)',
                 'rgba(75, 192, 192, 0.2)',
                 'rgba(153, 102, 255, 0.2)',
                 'rgba(255, 159, 64, 0.2)'
             ],
             borderColor: [
                 'rgba(255, 99, 132, 1)',
                 'rgba(54, 162, 235, 1)',
                 'rgba(255, 206, 86, 1)',
                 'rgba(75, 192, 192, 1)',
                 'rgba(153, 102, 255, 1)',
                 'rgba(255, 159, 64, 1)'
             ],
             borderWidth: 1
         }]
     },
     tooltips: {
           backgroundColor: "rgb(255,255,255)",
           bodyFontColor: "#858796",
           titleMarginBottom: 10,
           titleFontColor: '#6e707e',
           titleFontSize: 14,
           borderColor: '#dddfeb',
           borderWidth: 1,
           xPadding: 15,
           yPadding: 15,
           displayColors: false,
           intersect: false,
           mode: 'index',
           caretPadding: 10,
           callbacks: {
             label: function(tooltipItem, chart) {
               var datasetLabel = chart.datasets[tooltipItem.datasetIndex].label || '';
               return datasetLabel + ': $' + number_format(tooltipItem.yLabel);
             }
           }
     },

 });