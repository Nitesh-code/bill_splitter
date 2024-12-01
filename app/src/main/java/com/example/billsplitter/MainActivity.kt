package com.example.billsplitter

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.billsplitter.components.InputField
import com.example.billsplitter.ui.theme.BillSplitterTheme
import com.example.billsplitter.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            MyBillSplitter {
//                Text(text = "Hello again")
//            }
//            TotalPersonView()
            MainContent()
        }
    }
}

@Composable
fun MyBillSplitter(content: @Composable () -> Unit ){
    BillSplitterTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            color = MaterialTheme.colors.background
        ) {
            content()
        }
    }
}

//@Preview
@Composable
fun TotalPersonView(totalPerPerson: Double = 0.0){
    Surface(modifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
        .padding(15.dp)
        .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Color(color = 0xFFE9D7F7)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(text = stringResource(R.string.total_per_person),
            style = MaterialTheme.typography.h5)
            Text(text = "$${total}", style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.ExtraBold)
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun MainContent(){
    BillForm(){ billAmt ->
        
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(modifier: Modifier = Modifier,
             onValueChanged: (String) -> Unit ={}
             ){
    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.isNotEmpty()
    }


    val sliderPositionState = remember {
        mutableStateOf(0f)
    }

    val tipPercentage= (sliderPositionState.value *100).toInt()


    val splitCount = remember {
        mutableStateOf(1)
    }

    val tipAmountState = remember{
        mutableStateOf(0.0)
    }

    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }


    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {

        TotalPersonView(totalPerPerson = totalPerPersonState.value)

        Surface(
            modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(corner = CornerSize(8.dp)),
            border = BorderStroke(width = 1.dp, color = Color.LightGray)) {

            Column(modifier = Modifier
                .padding(6.dp)
                .fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                InputField(modifier = Modifier.fillMaxWidth(),
                    valueState = totalBillState,
                    labelId = "Enter Bill",
                    enabled = true,
                    isSingleLine = true,
                    onAction = KeyboardActions{
                        if (!validState) return@KeyboardActions

                        onValueChanged(totalBillState.value.trim())
                        keyboardController?.hide()
                    }
                )

            if (validState){
                Row(modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Split",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )

                    Spacer(modifier = Modifier.width(120.dp))

                    Row(modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End) {
                        RoundIconButton(
                            imageVector = Icons.Default.Remove,
                            onClick = {
                                splitCount.value = if (splitCount.value >1) splitCount.value -1 else 1
                                totalPerPersonState.value = calculateTotalPerPerson(tipAmountState.value, totalBillState.value, splitCount.value)
                            })

                        Text(text = "${splitCount.value}", modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 12.dp, end = 12.dp))

                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {
                                splitCount.value = splitCount.value +1
                                totalPerPersonState.value = calculateTotalPerPerson(tipAmountState.value, totalBillState.value, splitCount.value)
                            })
                    }
                }

                //Tip Row
                Row(modifier = Modifier.padding(horizontal = 3.dp, vertical = 12.dp)) {
                    Text(text = "Tip",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically))
                    Spacer(modifier = Modifier.width(200.dp))
                    Text(text = "$ ${tipAmountState.value}",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically))

                }

                Column(verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "$tipPercentage %")
                    Spacer(modifier = Modifier.height(14.dp))

                    //Slider
                    Slider(value = sliderPositionState.value, onValueChange = {
                        sliderPositionState.value = it
                        tipAmountState.value = calculateTotalTip(totalBillState.value, tipPercentage)
                        totalPerPersonState.value = calculateTotalPerPerson(tipAmountState.value, totalBillState.value, splitCount.value)
                    },
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        steps = 5)
                }

            } else {

            }

            }

        }
    }


}

fun calculateTotalPerPerson(tipAmount: Double, totalBill: String, splitCount: Int): Double {
    return (tipAmount + totalBill.toInt()) / splitCount
}

fun calculateTotalTip(value: String, tipPercentage: Int): Double {
    val totalBill: Double = value.toDouble()
    return if (totalBill > 1 && value.isNotEmpty()) (totalBill * tipPercentage) /100 else 0.0
}


//@Composable
//fun Count(count: Int=1, updateCounter: (Int) -> Unit){}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BillSplitterTheme {
        MyBillSplitter {
            Text(text = "Hello Again")
        }
    }
}