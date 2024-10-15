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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LeyesRobo(navController: NavHostController) {
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
    ){Column(
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
            text = "Robo",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(75.dp))

        Text(
            text = "ARTICULO 364.- COMETE EL DELITO DE ROBO: EL QUE SE APODERE DE UNA COSA MUEBLE, AJENA, SIN EL CONSENTIMIENTO DE QUIEN TENGA DERECHO A DISPONER DE ELLA.",
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "ARTICULO 365.- SE EQUIPARA AL ROBO, Y SE CASTIGARA COMO TAL:",
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = "I.- AL APODERAMIENTO O DESTRUCCIÓN DE UNA COSA MUEBLE, EJECUTADOS POR EL DUEÑO, SI LA COSA SE HALLA EN PODER DE OTRO A TÍTULO DE PRENDA O DE DEPÓSITO, DECRETADO POR UNA AUTORIDAD O HECHO CON SU INTERVENCIÓN;",
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = "II.- SI LA COSA ESTÁ EN PODER DE OTRO A RESULTAS DE CONTRATO PÚBLICO O PRIVADO, Y EL PROPIETARIO SE APODERA DE ELLA;",
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = "III.- EL APROVECHAMIENTO DE ENERGÍA ELÉCTRICA O CUALQUIER OTRO FLUIDO, EJECUTADO SIN DERECHO Y SIN CONSENTIMIENTO DE LA PERSONA QUE LEGALMENTE PUEDA DISPONER DE ÉL;",
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = "IV.- EL APODERAMIENTO MATERIAL O MEDIANTE VÍA ELECTRÓNICA DE LOS DOCUMENTOS QUE CONTENGAN DATOS EN COMPUTADORAS, O EL APROVECHAMIENTO O UTILIZACIÓN DE DICHOS DATOS, SIN DERECHO Y SIN CONSENTIMIENTO DE LA PERSONA QUE LEGALMENTE PUEDA DISPONER DE LOS MISMOS;",
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = "V.- A QUIEN REITERADAMENTE COMPRE OBJETOS ROBADOS, SE LE APLICARÁN LAS SANCIONES DEL DELITO DE ROBO; CONSIDERÁNDOSE PARA ESTE EFECTO LA ADQUISICIÓN POR MAS DE TRES VECES DE OBJETOS ROBADOS; O",
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(1.dp))

        Text(
            text = "VI.- EL APODERAMIENTO O USO INDEBIDO DE TARJETAS DE CRÉDITO O DÉBITO EXPEDIDAS POR INSTITUCIONES BANCARIAS O DE CUALQUIER OTRA NATURALEZA, O DE TÍTULOS DE CRÉDITO O DOCUMENTOS AUTÉNTICOS QUE SIRVAN PARA EL PAGO DE BIENES O SERVICIOS O PARA OBTENER DINERO EN EFECTIVO, SIN EL CONSENTIMIENTO DE QUIEN TENGA DERECHO A DISPONER DE TAL INSTRUMENTO Y CON EL QUE EL SUJETO ACTIVO PUEDA OBTENER UN BENEFICIO ECONÓMICO EN DETRIMENTO DE ALGUIEN.",
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "ARTICULO 365 BIS.- TAMBIÉN SE EQUIPARÁ (SIC) AL DELITO DE ROBO Y SE SANCIONARÁ CON PENA DE CINCO A QUINCE AÑOS DE PRISIÓN Y MULTA DE CIEN A MIL CUOTAS, CON INDEPENDENCIA DE LAS PENAS QUE LE CORRESPONDAN POR LA COMISIÓN DE OTROS DELITOS, AL QUE:",
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = "I.- DESMANTELE ALGÚN O ALGUNOS VEHÍCULOS ROBADOS Y/O COMERCIALICE CONJUNTA O SEPARADAMENTE SUS PARTES;",
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = "II.- ENAJENE O TRAFIQUE DE CUALQUIER MANERA CON VEHÍCULO O VEHÍCULOS REBADOS;",
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = "III.- DETENTE, POSEA O CUSTODIE ILEGÍTIMAMENTE UNO O MÁS VEHÍCULOS ROBADOS; O DETENTE, CUSTODIE, ALTERE O MODIFIQUE DE CUALQUIER MANERA LA DOCUMENTACIÓN QUE ACREDITE LA PROPIEDAD O IDENTIFICACIÓN DE UNO O MÁS VEHÍCULOS ROBADOS;",
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = "IV.- ALTERE, MODIFIQUE, SUSTITUYA O SUPRIMA DE CUALQUIER MANERA LOS NÚMEROS O LETRAS DE SERIES DEL MOTOR, CHASIS, CARROCERÍA O DE CUALQUIER PARTE, QUE SIRVA PARA IDENTIFICAR UNO O MÁS VEHÍCULOS ROBADOS;",
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = "V.- TRASLADE EL O LOS VEHÍCULOS ROBADOS A OTRA ENTIDAD FEDERATIVA O AL EXTRANJERO;",
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = "VI.- UTILICE EL O LOS VEHÍCULOS ROBADOS EN LA COMISIÓN DE OTRO U OTROS DELITOS.",
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "A QUIEN APORTE RECURSOS ECONÓMICOS O DE CUALQUIER ÍNDOLE PARA LA EJECUCIÓN DE LAS ACTIVIDADES DESCRITAS EN LAS FRACCIONES ANTERIORES, SE LE CONSIDERARÁ COPARTÍCIPE EN LOS TÉRMINOS DEL ARTÍCULO 39 DE ESTE CÓDIGO.",
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(Color.LightGray)
        )



    }
    }

}