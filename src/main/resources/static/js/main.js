var nombres = [];
var montos = [];
var total = 0;

function Calcular(){
    total = 0;
    apagar = 0;
    for(let i=0; i < montos.length; i++){
        total += parseFloat(montos[i]);
    }
    apagar = parseFloat(total/montos.length).toFixed(2);
    var totalM = document.getElementById("total");
    totalM.innerHTML = `
    <div class="flex-item">
    <p> Total Cost: ${total} </p>
    </div>    
    `;
    document.getElementById("cantidad").value = 1;

}

function Recalcular(cantidad){
    total = 0;
    apagar = 0;
    for(let i=0; i < montos.length; i++){
        total += parseFloat(montos[i]);
    }
    apagar = parseFloat(total/cantidad).toFixed(2);
    var totalM = document.getElementById("total");
    totalM.innerHTML = `
    <div class="flex-item">
    <p> Total Cost: ${total} </p>
    <p>Each one has to pay : ${apagar}</p>
    <p>People : ${cantidad} people</p>
    </div>    
    `;

}

function Agregar(nombre, monto){
    console.log(monto);
    if (monto == "" || nombre == "") {
        alert("ADD");
    }
    else{
        AgregarNuevo(nombre, monto);
    }
}

function AgregarNuevo(nombre, monto){
    nombres.push(nombre);
    montos.push(monto);
    Imprimir();
    Calcular();
    document.getElementById("nombre").value = "";
    document.getElementById("monto").value = "";
}

function BorrarUno(){
    let name = prompt("Eliminate name");
    if (name == ""){
        alert("ADD Name");
    }
    else{
    let indice = nombres.indexOf(name);
    if (indice == -1){
        alert("All eliminated");
    }else{
    nombres.splice(indice, 1);
    montos.splice(indice,1);
    Imprimir();
    Calcular();
    }}
}

function Imprimir(){
    let nuevo = document.getElementById("datos");
    nuevo.innerHTML = "";
    for (let i = 0; i < montos.length; i++){
        nuevo.innerHTML += `
        <div class="flex-item">
        <p>${nombres[i]} Payed: ${montos[i]}</p>
        </div>
    `;
    }
}

