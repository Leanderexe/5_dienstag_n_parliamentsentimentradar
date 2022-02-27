document.getElementById("starttime5").innerHTML=dat[0];
document.getElementById("endtime5").innerHTML=dat[1577];
document.getElementById("start5").value=0;
document.getElementById("end5").value=1577;
function updateStart5(range){
console.log(range.value);
document.getElementById("starttime5").innerHTML=dat[range.value];
};
function updateEnd5(range){
console.log(range.value);
document.getElementById("endtime5").innerHTML=dat[range.value];
};
rlab=[];
rdat=[];
rbc=[];
rboc=[];
var xhr6 = new XMLHttpRequest();
xhr6.onreadystatechange = function() {
        console.log(this.status);
        if (this.readyState == 4 && this.status == 200) {
            var spc6=JSON.parse(this.responseText);
            console.log(spc6);
            for(let i=0;i<spc6.length;i++){
              /*var xhr61 = new XMLHttpRequest();
              xhr61.onreadystatechange = function() {
                      console.log(this.status);
                      if (this.readyState == 4 && this.status == 200) {
                          var spc61=JSON.parse(this.responseText);
                          console.log(spc61);
                     }
                  };
              xhr61.open("GET", "http://localhost:4567/rede/rednerid/"+spc6._id, true);
              xhr61.send();*/
            }

       }
    };
xhr6.open("GET", "http://localhost:4567/redner", true);
xhr6.send();
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