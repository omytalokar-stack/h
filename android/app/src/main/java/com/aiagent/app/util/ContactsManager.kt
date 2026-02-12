package com.aiagent.app.util

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log

class ContactsManager {
    companion object {
        private const val TAG = "ContactsManager"
        
        fun getContactName(context: Context, phoneNumber: String?): String? {
            if (phoneNumber == null) return null
            
            return try {
                val uri = ContactsContract.PhoneLookup.CONTENT_FILTER_URI.buildUpon()
                    .appendPath(phoneNumber)
                    .build()
                
                val cursor: Cursor? = context.contentResolver.query(
                    uri,
                    arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME),
                    null,
                    null,
                    null
                )
                
                var contactName: String? = null
                if (cursor != null && cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        contactName = cursor.getString(nameIndex)
                    }
                    cursor.close()
                }
                
                Log.d(TAG, "Contact name for $phoneNumber: $contactName")
                contactName
            } catch (e: Exception) {
                Log.e(TAG, "Error getting contact name: ${e.message}")
                null
            }
        }
        
        fun getAllContacts(context: Context): List<Pair<String, String>> {
            val contacts = mutableListOf<Pair<String, String>>()
            
            return try {
                val cursor: Cursor? = context.contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    arrayOf(
                        ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.Contacts.HAS_PHONE_NUMBER
                    ),
                    null,
                    null,
                    ContactsContract.Contacts.DISPLAY_NAME
                )
                
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        val id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                        val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                        val hasPhone = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                        
                        if (hasPhone > 0) {
                            val phoneCursor = context.contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                arrayOf(id),
                                null
                            )
                            
                            if (phoneCursor != null && phoneCursor.moveToFirst()) {
                                val phone = phoneCursor.getString(
                                    phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                )
                                contacts.add(Pair(name, phone))
                                phoneCursor.close()
                            }
                        }
                    } while (cursor.moveToNext())
                    cursor.close()
                }
                
                Log.d(TAG, "Retrieved ${contacts.size} contacts")
                contacts
            } catch (e: Exception) {
                Log.e(TAG, "Error getting contacts: ${e.message}")
                contacts
            }
        }
    }
}
