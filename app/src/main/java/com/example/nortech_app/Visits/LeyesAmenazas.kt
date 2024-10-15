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
fun LeyesAmenazas(navController: NavHostController) {
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
                text = "Amenazas",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(75.dp))

            Text(
                text = "ARTICULO 291.- COMETE EL DELITO DE AMENAZAS:",
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "I.- QUIEN UTILIZANDO MEDIOS ELECTRÓNICOS O DE CUALQUIER OTRO MODO AMENACE A OTRO CON CAUSARLE UN MAL EN SU PERSONA, EN SUS BIENES, EN SU HONOR O EN SUS DERECHOS, O EN LA PERSONA, HONOR, BIENES O DERECHOS DE ALGUIEN CON QUIEN ESTÉ LIGADO POR ALGÚN VÍNCULO;",
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.height(1.dp))
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
                text = "II.- QUIEN, POR MEDIO DE AMENAZAS DE CUALQUIER ÍNDOLE, TRATE DE IMPEDIR QUE OTRO EJECUTE LO QUE TIENE DERECHO A HACER, O INTENTE OBLIGAR A OTRO A EJECUTAR CONDUCTAS DELICTIVAS.",
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = "PARA LOS EFECTOS DE ESTA DISPOSICIÓN, AMENAZA ES TODA CONDUCTA REALIZADA QUE PERTURBE LA TRANQUILIDAD DE ÁNIMO DE LA VÍCTIMA O QUE PRODUZCA ZOZOBRA O PERTURBACIÓN PSÍQUICA EN LA MISMA, POR TEMOR A QUE SE LE CAUSE UN MAL INMEDIATO O FUTURO.",
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = "NO SE PODRÁ PROCEDER CONTRA EL AUTOR DE ESTE DELITO SIN QUE EXISTA PREVIA QUERELLA DE LA PERSONA OFENDIDA.",
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color.LightGray)
            )

        }
    }

}