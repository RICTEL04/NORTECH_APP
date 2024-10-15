import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.nortech_app.Visits.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HumanRightsView(navController: NavHostController) {
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            // Título principal
            Text(
                text = "Leyes del trabajo",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Cita
            Text(
                text = "\"El trabajo digno es un derecho humano fundamental y debe ser respetado en todas sus formas.\"\n\n" +
                        "(Constitución Política de los Estados Unidos Mexicanos, Artículo 123)",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Descripción principal
            Text(
                text = "Las leyes laborales están diseñadas para garantizar los derechos de los trabajadores y empleadores " +
                        "en el entorno laboral. Estas leyes cubren temas como la jornada laboral, el salario mínimo, las condiciones " +
                        "de trabajo, la seguridad social, el derecho a la sindicalización y la resolución de conflictos laborales.\n\n" +
                        "Los trabajadores tienen derecho a un salario justo y equitativo, a condiciones laborales seguras y saludables, " +
                        "así como a jornadas laborales que no excedan los límites establecidos por la ley. Los empleadores, por otro lado, " +
                        "tienen la obligación de respetar estos derechos y proporcionar un ambiente de trabajo adecuado.\n\n" +
                        "En México, las principales leyes que regulan el trabajo incluyen la Constitución Política de los Estados Unidos " +
                        "Mexicanos, la Ley Federal del Trabajo, y diversas normativas relacionadas con la seguridad social y la protección " +
                        "de los trabajadores.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Expandable Card Section
            ExpandableCard(title = "Derechos de los Trabajadores")
            ExpandableCard(title = "Obligaciones de los Empleadores")
        }
    }
}

@Composable
fun ExpandableCard(title: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth()
            )
            IconButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
            }

            if (expanded) {
                Text(
                    text = "Contenido de $title", // Aquí puedes colocar más texto o composables
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}