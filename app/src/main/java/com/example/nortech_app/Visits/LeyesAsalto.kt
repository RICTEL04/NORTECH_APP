package com.example.nortech_app.Visits

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LeyesAsalto(navController: NavHostController) {
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("Derechos")
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController, 2)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color.White)
                .verticalScroll(scrollState),

            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Text(
                text = "Asalto",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(75.dp))

            Text(
                text = "ARTICULO 297.- COMETE EL DELITO DE ASALTO, EL QUE EN DESPOBLADO O PARAJE SOLITARIO HAGA USO DE LA VIOLECIA SOBRE UNA PERSONA, CON EL PROPOSITO DE CAUSARLE UN MAL, OBTENER UN LUCRO O EXIGIR SU ASENTIMIENTO PARA CUALQUIER FIN Y CUALESQUIERA QUE SEAN LOS MEDIOS Y EL GRADO DE VIOLENCIA QUE SE EMPLEE, INDEPENDIENTEMENTE DE CUALQUIER HECHO DELICTUOSO QUE RESULTE COMETIDO.",
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.height(10.dp))


            Text(
                text = "SE ENTIENDE POR PARAJE SOLITARIO, NO SOLAMENTE EL QUE ESTE DESHABITADO, SINO CUALQUIERA EN QUE POR RAZON DE LA HORA O CIRCUNSTANCIA, EL PASIVO SE ENCUENTRE EN INFERIORIDAD MANIFIESTA.",
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = "ARTICULO 298.- A LOS RESPONSABLES DEL DELITO DE ASALTO SE LES IMPONDRA SANCION DE UNO A SEIS AÑOS DE PRISION, Y MULTA DE CINCO A CINCUENTA CUOTAS.",
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = "ARTICULO 299.- SI LOS SALTEADORES ATACARAN UNA POBLACION, SE APLICARAN DE DIEZ A QUINCE AÑOS DE PRISION A LOS CABECILLAS O JEFES, Y DE OCHO A DIEZ AÑOS A LOS DEMAS.",
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = "ARTÍCULO 299 BIS.- CUANDO EL DELITO DE ASALTO SE COMETA EN EL INTERIOR DE UNA UNIDAD DEL SERVICIO PÚBLICO DE TRANSPORTE DE PASAJEROS O CUALQUIERA QUE PRESTE SERVICIOS SIMILARES, A LA PENA QUE CORRESPONDA SE AUMENTARÁ DE SEIS MESES A CUATRO AÑOS DE PRISIÓN.",
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color.LightGray)
            )

        }
    }

}