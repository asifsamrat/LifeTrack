import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.lifetrack.ui.components.TopHeader


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Top Header Section
        TopHeader(
//            onNotificationClick = {
//                navController.navigate("notification")
//            }
        )

        // Profile Section
        ProfileInfo()

        // Upcomming reminder and memeories Time line
        TimeLineScreen()
    }
}
