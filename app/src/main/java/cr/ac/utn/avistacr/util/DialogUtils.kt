package cr.ac.utn.avistacr.util

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import cr.ac.utn.avistacr.R

object DialogUtils {

    fun showDialogCondition(context: Context, questionText: String, callback: () -> Unit) {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setMessage(questionText)
            .setCancelable(false)
            .setPositiveButton(context.getString(R.string.action_yes), DialogInterface.OnClickListener {
                    dialog, id -> callback()
            })
            .setNegativeButton(context.getString(R.string.action_no), DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })

        val alert = dialogBuilder.create()
        alert.setTitle(context.getString(R.string.dialog_question_title))
        alert.show()
    }
}
