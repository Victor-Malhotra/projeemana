package vc.android.projeemana.activities

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vc.android.projeemana.utils.Constants
import vc.android.projeemana.R
import vc.android.projeemana.adapters.MemberListItemsAdapter
import vc.android.projeemana.firebase.FirestoreClass
import vc.android.projeemana.models.Board
import vc.android.projeemana.models.User

class MembersActivity : BaseActivity() {


    // START
    // A global variable for Board Details.
    private lateinit var mBoardDetails: Board
    // END

    private lateinit var mAssignedMembersList: ArrayList<User>

    private var anyChangesDone: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)


        // START
        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        // END


        // START
        setupActionBar()
        // END

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAssignedMembersListDetails(
            this@MembersActivity,
            mBoardDetails.assignedTo
        )
    }


    // START
    /**
     * A function to setup action bar
     */
    private fun setupActionBar() {

        val toolbarMembersActivity = findViewById<Toolbar>(R.id.toolbar_members_activity)

        setSupportActionBar(toolbarMembersActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbarMembersActivity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu to use in the action bar
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    fun setupMembersList(list: ArrayList<User>) {


        // START
        mAssignedMembersList = list
        // END

        hideProgressDialog()

        val rvMembersList = findViewById<RecyclerView>(R.id.rv_members_list)

        rvMembersList.layoutManager = LinearLayoutManager(this@MembersActivity)
        rvMembersList.setHasFixedSize(true)

        val adapter = MemberListItemsAdapter(this@MembersActivity, list)
        rvMembersList.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.action_add_member -> {


                // START
                dialogSearchMember()
                // END
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (anyChangesDone) {
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    private fun dialogSearchMember() {
        val dialog = Dialog(this)
        /*Set the screen content from a layout resource.
    The resource will be inflated, adding all top-level views to the screen.*/
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.findViewById<TextView>(R.id.tv_add).setOnClickListener {

            val email = dialog.findViewById<EditText>(R.id.et_email_search_member).text.toString()

            if (email.isNotEmpty()) {
                dialog.dismiss()

                // START
                // Show the progress dialog.
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().getMemberDetails(this@MembersActivity, email)
                // END
            } else {
                showErrorSnackBar("Please enter members email address.")
                /*Toast.makeText(
                    this@MembersActivity,
                    "Please enter members email address.",
                    Toast.LENGTH_SHORT
                ).show()*/
            }
        }
        dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dialog.dismiss()
        }
        //Start the dialog and display it on screen.
        dialog.show()
    }

    fun memberDetails(user: User) {


        // START
        mBoardDetails.assignedTo.add(user.id)


        // START
        FirestoreClass().assignMemberToBoard(this@MembersActivity, mBoardDetails, user)
        // ENDss
    }
    // END


    // START
    /**
     * A function to get the result of assigning the members.
     */
    fun memberAssignSuccess(user: User) {

        hideProgressDialog()

        mAssignedMembersList.add(user)

        anyChangesDone = true

        setupMembersList(mAssignedMembersList)
    }
}