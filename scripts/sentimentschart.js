var ctx = document.getElementById("sentimentschart");
var radarChart = new Chart(ctx, {
  type: 'radar',
  data: {
          labels: ["English", "Maths", "Physics", "Chemistry", "Biology", "History"],
          datasets: [{
            label: "Student A",
            backgroundColor: "rgba(200,0,0,0.2)",
            data: [65, 75, 70, 80, 60, 80]
          }, {
            label: "Student B",
            backgroundColor: "rgba(0,0,200,0.2)",
            data: [54, 65, 60, 70, 70, 75]
          }]
        };
});