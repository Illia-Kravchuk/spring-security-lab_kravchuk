package ua.kpi.its.lab.security.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import ua.kpi.its.lab.security.dto.DisciplineRequest
import ua.kpi.its.lab.security.dto.EducationalInstitutionRequest
import ua.kpi.its.lab.security.dto.EducationalInstitutionResponse

@Composable
fun EducationalInstitutionScreen(
    token: String,
    scope: CoroutineScope,
    client: HttpClient,
    snackbarHostState: SnackbarHostState
) {
    var institutions by remember { mutableStateOf<List<EducationalInstitutionResponse>>(listOf()) }
    var loading by remember { mutableStateOf(false) }
    var openDialog by remember { mutableStateOf(false) }
    var selectedInstitution by remember { mutableStateOf<EducationalInstitutionResponse?>(null) }

    LaunchedEffect(token) {
        loading = true
        delay(1000)
        institutions = withContext(Dispatchers.IO) {
            try {
                val response = client.get("http://localhost:8080/institutions") {
                    bearerAuth(token)
                }
                loading = false
                response.body()
            }
            catch (e: Exception) {
                val msg = e.toString()
                snackbarHostState.showSnackbar(msg, withDismissAction = true, duration = SnackbarDuration.Indefinite)
                institutions
            }
        }
    }

    if (loading) {
        LinearProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        )
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedInstitution = null
                    openDialog = true
                },
                content = {
                    Icon(Icons.Filled.Add, "add institution")
                }
            )
        }
    ) {
        if (institutions.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Text("No institutions to show", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
        }
        else {
            LazyColumn(
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant).fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(institutions) { institution ->
                    InstitutionItem(
                        institution = institution,
                        onEdit = {
                            selectedInstitution = institution
                            openDialog = true
                        },
                        onRemove = {
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    try {
                                        val response = client.delete("http://localhost:8080/institutions/${institution.id}") {
                                            bearerAuth(token)
                                        }
                                        require(response.status.isSuccess())
                                    }
                                    catch(e: Exception) {
                                        val msg = e.toString()
                                        snackbarHostState.showSnackbar(msg, withDismissAction = true, duration = SnackbarDuration.Indefinite)
                                    }
                                }

                                loading = true

                                institutions = withContext(Dispatchers.IO) {
                                    try {
                                        val response = client.get("http://localhost:8080/institutions") {
                                            bearerAuth(token)
                                        }
                                        loading = false
                                        response.body()
                                    }
                                    catch (e: Exception) {
                                        val msg = e.toString()
                                        snackbarHostState.showSnackbar(msg, withDismissAction = true, duration = SnackbarDuration.Indefinite)
                                        institutions
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }

        if (openDialog) {
            InstitutionDialog(
                institution = selectedInstitution,
                token = token,
                scope = scope,
                client = client,
                onDismiss = {
                    openDialog = false
                },
                onError = {
                    scope.launch {
                        snackbarHostState.showSnackbar(it, withDismissAction = true, duration = SnackbarDuration.Indefinite)
                    }
                },
                onConfirm = {
                    openDialog = false
                    loading = true
                    scope.launch {
                        institutions = withContext(Dispatchers.IO) {
                            try {
                                val response = client.get("http://localhost:8080/institutions") {
                                    bearerAuth(token)
                                }
                                loading = false
                                response.body()
                            }
                            catch (e: Exception) {
                                loading = false
                                institutions
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun InstitutionDialog(
    institution: EducationalInstitutionResponse?,
    token: String,
    scope: CoroutineScope,
    client: HttpClient,
    onDismiss: () -> Unit,
    onError: (String) -> Unit,
    onConfirm: () -> Unit,
) {
    val discipline = institution?.disciplines

    var name by remember { mutableStateOf(institution?.name ?: "") }
    var accreditationLevel by remember { mutableStateOf(institution?.accreditationLevel ?: "") }
    var address by remember { mutableStateOf(institution?.address ?: "") }
    var foundationDate by remember { mutableStateOf(institution?.foundationDate ?: "") }
    var facultiesCount by remember { mutableStateOf(institution?.facultiesCount?.toString() ?: "") }
    var website by remember { mutableStateOf(institution?.website ?: "") }
    var hasMilitaryDepartment by remember { mutableStateOf(institution?.hasMilitaryDepartment ?: false) }
    var disciplineName by remember { mutableStateOf(discipline?.name ?: "") }
    var disciplineInstitution by remember { mutableStateOf(discipline?.institution ?: "") }
    var disciplineSpecialityCode by remember { mutableStateOf(discipline?.specialityCode ?: "") }
    var disciplineSemester by remember { mutableStateOf(discipline?.semester?.toString() ?: "") }
    var disciplineHoursCount by remember { mutableStateOf(discipline?.hoursCount?.toString() ?: "") }
    var disciplineApprovalDate by remember { mutableStateOf(discipline?.approvalDate ?: "") }
    var disciplineHasExam by remember { mutableStateOf(discipline?.hasExam ?: false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.padding(16.dp).wrapContentSize()) {
            Column(
                modifier = Modifier.padding(16.dp, 8.dp).width(IntrinsicSize.Max).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (institution == null) {
                    Text("Create institution")
                }
                else {
                    Text("Update institution")
                }

                HorizontalDivider()
                Text("Institution info")
                TextField(name, { name = it }, label = { Text("Name") })
                TextField(accreditationLevel, { accreditationLevel = it }, label = { Text("Accreditation Level") })
                TextField(address, { address = it }, label = { Text("Address") })
                TextField(foundationDate, { foundationDate = it }, label = { Text("Foundation date") })
                TextField(facultiesCount, { facultiesCount = it }, label = { Text("Faculties count") })
                TextField(website, { website = it }, label = { Text("Website") })
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(hasMilitaryDepartment, { hasMilitaryDepartment = it })
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Has Military Department")
                }

                HorizontalDivider()
                Text("Discipline info")
                TextField(disciplineName, { disciplineName = it }, label = { Text("Name") })
                TextField(disciplineInstitution, { disciplineInstitution = it }, label = { Text("Institution") })
                TextField(disciplineSpecialityCode, { disciplineSpecialityCode = it }, label = { Text("Speciality Code") })
                TextField(disciplineSemester, { disciplineSemester = it }, label = { Text("Semester") })
                TextField(disciplineHoursCount, { disciplineHoursCount = it }, label = { Text("Hours Count") })
                TextField(disciplineApprovalDate, { disciplineApprovalDate = it }, label = { Text("Approval Date") })
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(disciplineHasExam, { disciplineHasExam = it })
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Has Exam")
                }

                HorizontalDivider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.fillMaxWidth(0.1f))
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            scope.launch {
                                try {
                                    val request = EducationalInstitutionRequest(
                                        name, accreditationLevel, address, foundationDate, facultiesCount.toInt(), website, hasMilitaryDepartment,
                                        DisciplineRequest(
                                            disciplineName, disciplineInstitution, disciplineSpecialityCode, disciplineSemester.toInt(),
                                            disciplineHoursCount.toInt(), disciplineApprovalDate, disciplineHasExam
                                        )
                                    )
                                    val response = if (institution == null) {
                                        client.post("http://localhost:8080/institutions") {
                                            bearerAuth(token)
                                            setBody(request)
                                            contentType(ContentType.Application.Json)
                                        }
                                    } else {
                                        client.put("http://localhost:8080/institutions/${institution.id}") {
                                            bearerAuth(token)
                                            setBody(request)
                                            contentType(ContentType.Application.Json)
                                        }
                                    }
                                    require(response.status.isSuccess())
                                    onConfirm()
                                }
                                catch (e: Exception) {
                                    val msg = e.toString()
                                    onError(msg)
                                }
                            }
                        }
                    ) {
                        if (institution == null) {
                            Text("Create")
                        }
                        else {
                            Text("Update")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InstitutionItem(institution: EducationalInstitutionResponse, onEdit: () -> Unit, onRemove: () -> Unit) {
    Card(shape = CardDefaults.elevatedShape, elevation = CardDefaults.elevatedCardElevation()) {
        ListItem(
            overlineContent = {
                Text(institution.name)
            },
            headlineContent = {
                Text(institution.accreditationLevel)
            },
            supportingContent = {
                Text("Website: ${institution.website}")
            },
            trailingContent = {
                Row(modifier = Modifier.padding(0.dp, 20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clip(CircleShape).clickable(onClick = onEdit)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clip(CircleShape).clickable(onClick = onRemove)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        )
    }
}
