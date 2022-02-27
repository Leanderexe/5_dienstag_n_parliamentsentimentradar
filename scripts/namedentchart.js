nlab=[];
ndat=[];
nbc=[];
nboc=[];
var xhr51 = new XMLHttpRequest();
xhr51.onreadystatechange = function() {
        console.log(this.status);
        if (this.readyState == 4 && this.status == 200) {
            var spc51=JSON.parse(this.responseText);
            console.log(spc51);
            }
       };
xhr51.open("GET", "http://localhost:4567/namedentities", true);
xhr51.send();
var xhr5 = new XMLHttpRequest();
xhr5.onreadystatechange = function() {
        console.log(this.status);
        if (this.readyState == 4 && this.status == 200) {
            var spc5=JSON.parse(this.responseText);
            console.log(spc5);
            for(let i=0;i<spc5.length;i++){
                nlab.push(spc5[i].POS);
                ndat.push(spc5[i].Häufigkeit);
                nbc.push('rgba('+(42+i)%255+', '+(i)%255+', '+(202+i)%255+' 0.2)');
                nboc.push('rgba('+(42+i)%255+', '+(i)%255+', '+(202+i)%255+' 1)');
            }

       }
    };
xhr5.open("GET", "http://localhost:4567/namedentitiesobjects", true);
xhr5.send();
var ctx = document.getElementById("namedentchart");
var neChart = new Chart(ctx, {
  type: 'line',
  data: {
    labels: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
    datasets: [{
      label: "Earnings",
      lineTension: 0.1,
      backgroundColor: "rgba(78, 115, 223, 0.05)",
      borderColor: "rgba(78, 115, 223, 1)",
      pointRadius: 3,
      pointBackgroundColor: "rgba(78, 115, 223, 1)",
      pointBorderColor: "rgba(78, 115, 223, 1)",
      pointHoverRadius: 3,
      pointHoverBackgroundColor: "rgba(78, 115, 223, 1)",
      pointHoverBorderColor: "rgba(78, 115, 223, 1)",
      pointHitRadius: 10,
      pointBorderWidth: 2,
      data: [10000, 10000, 5000, 15000, 10000, 20000, 15000, 25000, 20000, 30000, 25000, 10000],
    },
    {
          label: "Costs",
          lineTension: 0.1,
          backgroundColor: "rgba(178, 15, 223, 0.05)",
          borderColor: "rgba(178, 015, 223, 1)",
          pointRadius: 3,
          pointBackgroundColor: "rgba(178, 15, 223, 1)",
          pointBorderColor: "rgba(178, 15, 223, 1)",
          pointHoverRadius: 3,
          pointHoverBackgroundColor: "rgba(178, 15, 223, 1)",
          pointHoverBorderColor: "rgba(178, 15, 223, 1)",
          pointHitRadius: 10,
          pointBorderWidth: 2,
          data: [1000, 20000, 15000, 22000, 20000, 30000, 35000, 42000, 44000, 46000, 60000, 55000],
        }],
  },
  options: {
    maintainAspectRatio: false,
    layout: {
      padding: {
        left: 10,
        right: 25,
        top: 25,
        bottom: 0
      }
    },
    scales: {
      xAxes: [{
        time: {
          unit: 'date'
        },
        gridLines: {
          display: false,
          drawBorder: false
        },
        ticks: {
          maxTicksLimit: 7
        }
      }],
      yAxes: [{
        ticks: {
          maxTicksLimit: 5,
          padding: 10,
          // Include a dollar sign in the ticks
          callback: function(value, index, values) {
            return '$' + number_format(value);
          }
        },
        gridLines: {
          color: "rgb(234, 236, 244)",
          zeroLineColor: "rgb(234, 236, 244)",
          drawBorder: false,
          borderDash: [2],
          zeroLineBorderDash: [2]
        }
      }],
    },
    legend: {
      display: false
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
    }
  }
});