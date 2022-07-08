package com.example.speechtotextandtexttospeech

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var textspeech: TextToSpeech
    lateinit var twointotwo: TextView
    lateinit var readQues: Button

    lateinit var speechRecognizer: SpeechRecognizer
    lateinit var userAnswer: EditText
    lateinit var micButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            checkPermission()
        }

        userAnswer = findViewById(R.id.userAnswer)
        micButton = findViewById(R.id.micButton)
        readQues = findViewById(R.id.readQues)
        twointotwo = findViewById(R.id.twointotwotext)

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {}
            override fun onBeginningOfSpeech() {
                userAnswer.setText("")
                userAnswer.setHint("Listening...")
            }

            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray) {}
            override fun onEndOfSpeech() {}
            override fun onError(i: Int) {}
            override fun onResults(bundle: Bundle) {

                micButton.setImageResource(R.drawable.ic_baseline_mic_off_24)
                val data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                userAnswer.setText(data!![0])
                speak(userAnswer.text.toString(),false)
            }

            override fun onPartialResults(bundle: Bundle) {}
            override fun onEvent(i: Int, bundle: Bundle) {}
        })

        micButton.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                speechRecognizer.stopListening()
            }
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                micButton.setImageResource(R.drawable.ic_baseline_mic_24)
                speechRecognizer.startListening(speechRecognizerIntent)
            }
            false
        })


        textspeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textspeech!!.setLanguage(Locale.ENGLISH)
                if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    Log.e("TTS", "Language not supported")
                } else {
                    readQues.setEnabled(true)
                }
            } else {
                Log.e("TTS", "Initialization failed")
            }
        }

        readQues.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {

                speak("two into two",true)
            }
        })

    }

    private fun speak(txt : String, ques:Boolean) {

        textspeech!!.setPitch(1.0f)
        textspeech!!.setSpeechRate(1.0f)

        if(ques)
        {
            textspeech!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null)
        }
        else
        {
            if(txt.equals("4"))
            {
                textspeech!!.speak("correct", TextToSpeech.QUEUE_FLUSH, null)
            }
            else
            {
                textspeech!!.speak("incorrect", TextToSpeech.QUEUE_FLUSH, null)
            }
        }
    }

    override fun onDestroy() {
        if (textspeech != null) {
            textspeech!!.stop()
            textspeech!!.shutdown()
        }
        super.onDestroy()
        speechRecognizer!!.destroy()
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RecordAudioRequestCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RecordAudioRequestCode && grantResults.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) Toast.makeText(
                this,
                "Permission Granted",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        const val RecordAudioRequestCode = 1
    }
}