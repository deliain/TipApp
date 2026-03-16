package com.example.tipapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tipapp.ui.theme.TipAppTheme
import com.example.tipapp.widget.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApp {
                MainContent()
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    TipAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding).verticalScroll(rememberScrollState())
            ) {
                content()
            }
        }
    }
}

//@Preview
@Composable
fun TopBanner(totalPerPerson: Double = 100.0) {
     Surface(modifier = Modifier.
     fillMaxWidth().
     height(200.dp).
     padding(top= 40.dp, start = 12.dp, end = 12.dp).
     clip(shape = RoundedCornerShape(corner = CornerSize(20.dp))), color = Color(0xFF2c556b)) {
         Column(modifier = Modifier.padding(12.dp),
             horizontalAlignment = Alignment.CenterHorizontally,
             verticalArrangement = Arrangement.Center) {
             val total = "%.2f".format(totalPerPerson)
             Text(modifier = Modifier.
                 padding(8.dp),
                     text = "Total per Person",
                     color = Color.White,
                     fontWeight = FontWeight.ExtraBold)
             Text(modifier = Modifier.
                    padding(8.dp),
                     text = "€$total",
                     color = Color.White,
                     fontWeight = FontWeight.ExtraBold,
                     fontSize = 32.sp)
         }
     }
}

@Composable
fun MainContent (){
    var peopleCounter = rememberSaveable() {
        mutableStateOf(1)
    }
    BillForm(peopleCounter = peopleCounter.value,
        updatePeopleCounter = { newValue ->
            peopleCounter.value = newValue
        })
}
@Composable
fun PeopleRow(peopleCounter: Int, updatePeopleCounter: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(start = 20.dp),
        horizontalArrangement = Arrangement.Start) {
        Text(text = "People", modifier = Modifier.align(alignment = Alignment.CenterVertically))
        Spacer(modifier = Modifier.width(50.dp))

        // Number of People Row
        Row(modifier = Modifier.padding(horizontal = 3.dp),
            horizontalArrangement = Arrangement.End) {

            RoundIconButton(
                modifier = Modifier,
                imageVector = androidx.compose.material.icons.Icons.Rounded.Remove,
                onClick =  {
                    updatePeopleCounter(if (peopleCounter > 0) peopleCounter - 1 else 0)

                })

            Text(text = "$peopleCounter", modifier = Modifier.align(alignment = Alignment.CenterVertically).padding(start = 10.dp, end= 10.dp))

            RoundIconButton(
                modifier = Modifier,
                imageVector = androidx.compose.material.icons.Icons.Rounded.Add,
                onClick =  {
                    updatePeopleCounter(peopleCounter+1)
                })
        }
    }
}

@Composable
fun TipRow(tipAmount: Double) {
    Row(modifier = Modifier.fillMaxWidth().padding(start = 20.dp),
        horizontalArrangement = Arrangement.Start) {
        Text(text = "Tip", modifier = Modifier.align(alignment = Alignment.CenterVertically))
        Spacer(modifier = Modifier.width(120.dp))
        Text(text = "€${"%.2f".format(tipAmount)}", modifier = Modifier.align(alignment = Alignment.CenterVertically))
    }
}
@Composable
fun PercentageColumn(sliderPositionState1: MutableState<Float>) {
    val percentage: Double = sliderPositionState1.value.toDouble()
    Column(modifier = Modifier.fillMaxWidth().padding(start = 20.dp, top = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text ="${"%.2f".format(percentage)}%")
        Spacer(modifier = Modifier.height(20.dp))
        Slider(value = sliderPositionState1.value,
            onValueChange = {newValue ->
                sliderPositionState1.value = newValue
                Log.d("SLIDER","$newValue")
            },
            valueRange = 0f..20f,
            steps = 5,
            modifier = Modifier.padding(end = 20.dp),
            colors = androidx.compose.material3.SliderDefaults.colors(
                thumbColor = Color(0xFF2c556b),
                activeTrackColor = Color(0xFF2c556b),
                inactiveTrackColor = Color(0xFF2c556b)
            ))
    }
}

@Composable
fun BillForm(modifier: Modifier = Modifier,
             onValueChange: (String) -> Unit = {}, peopleCounter: Int, updatePeopleCounter: (Int) -> Unit) {
    val totalBillState = rememberSaveable() {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val sliderPositionState = rememberSaveable() {
        mutableStateOf(0f)
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    //Calculate the values here
    val bill = totalBillState.value.toDoubleOrNull() ?: 0.0
    val tipAmount = (bill * sliderPositionState.value) / 100
    val totalPerPerson = (bill + tipAmount) / (if (peopleCounter > 0) peopleCounter else 1)

    TopBanner(totalPerPerson= totalPerPerson)
    Surface(modifier = Modifier.padding(2.dp).fillMaxWidth().padding(top= 40.dp, start = 12.dp, end = 12.dp),
        shape = RoundedCornerShape(corner = CornerSize(12.dp)),
        border = BorderStroke(2.dp, color = Color(0xFF2c556b))

    ) {

        Column(modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {

            com.example.tipapp.components.InputField(
                modifier = Modifier.fillMaxWidth(),
                valueState = totalBillState,
                labelId = "Enter Amount",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValueChange(totalBillState.value.trim())
                    keyboardController?.hide()
                }
            )
            if (validState) {
                //People Row
                PeopleRow(peopleCounter, updatePeopleCounter)
                //Spacer
                Spacer(modifier = Modifier.height(20.dp))
                // Tip Row
                TipRow(tipAmount= tipAmount)
                //Percentage & Slider Column
                PercentageColumn(sliderPositionState)
            }
        }
    }
}