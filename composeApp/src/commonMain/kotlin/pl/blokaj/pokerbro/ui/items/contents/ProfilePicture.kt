package pl.blokaj.pokerbro.ui.items.contents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import pl.blokaj.pokerbro.ui.items.components.ProfilePictureComponent

@Composable
fun ProfilePicture(
    profilePictureComponent: ProfilePictureComponent
) {
    val path by profilePictureComponent.profilePicturePath.subscribeAsState()

    Box(
        modifier = Modifier
            .size(130.dp)      // set the size of the circle
            .clip(CircleShape)
    ) {
        if (path.isNotEmpty()) {
            AsyncImage(
                model = path,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .fillMaxSize()   // fill the Box
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                Icons.Default.Person,
                contentDescription = "Default picture",
                modifier = Modifier
                    .fillMaxSize()  // fill the Box
                    .padding(8.dp) // optional padding for icon
            )
        }
    }
}