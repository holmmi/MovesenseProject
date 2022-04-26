package fi.metropolia.movesense.view.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AppRegistration
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.movesense.R
import fi.metropolia.movesense.model.api.LoginRequest


@ExperimentalMaterial3Api
@Composable
fun SettingsView(navController: NavController, settingsViewModel: SettingsViewModel = viewModel()) {
    var showLoginDialog by rememberSaveable { mutableStateOf(false) }
    // var loginDetails by rememberSaveable { mutableStateOf<LoginRequest?>(null)}
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = stringResource(id = R.string.settings)) }
            )
        },
        content = {
            Column(
                modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showLoginDialog) {
                    Dialog(
                        onDismissRequest = { showLoginDialog = false },
                        content = {
                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.background)
                                    .fillMaxHeight(0.7f).fillMaxWidth(0.9f)
                            ) {
                                Column(
                                    Modifier
                                        .fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    OutlinedTextField(
                                        modifier = Modifier.padding(8.dp),
                                        value = username,
                                        onValueChange = { username = it },
                                        placeholder = { Text(text = stringResource(id = R.string.username)) }
                                    )
                                    OutlinedTextField(
                                        modifier = Modifier.padding(8.dp),
                                        value = password,
                                        onValueChange = { password = it },
                                        visualTransformation = VisualTransformation.None,
                                        placeholder = { Text(text = stringResource(id = R.string.password)) }
                                    )

                                    Spacer(modifier = Modifier.weight(1f, false))

                                    Row(horizontalArrangement = Arrangement.End) {
                                        TextButton(onClick = { /*TODO: Send user login details*/ }) {
                                            Text(text = stringResource(id = R.string.login))
                                        }
                                        TextButton(onClick = { showLoginDialog = false }) {
                                            Text(text = stringResource(id = R.string.cancel))
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
                OutlinedButton(
                    onClick = { showLoginDialog = true },
                    modifier = Modifier
                        .padding(8.dp)
                        .width(150.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Login,
                        contentDescription = stringResource(id = R.string.login)
                    )
                    Text(text = stringResource(id = R.string.login))
                }
                OutlinedButton(
                    onClick = { /*TODO: navigate to register page*/ },
                    modifier = Modifier
                        .padding(8.dp)
                        .width(150.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AppRegistration,
                        contentDescription = stringResource(id = R.string.register)
                    )
                    Text(text = stringResource(id = R.string.register))
                }
                OutlinedButton(
                    onClick = { /*TODO: navigate to settings page*/ },
                    modifier = Modifier
                        .padding(8.dp)
                        .width(150.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = stringResource(id = R.string.settings)
                    )
                    Text(text = stringResource(id = R.string.settings))
                }
            }
        }
    )
}
