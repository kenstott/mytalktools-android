/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.MTA.MyTalkMobile.Utilities.Utility;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

// TODO: Auto-generated Javadoc

/**
 * The Class UtterPhrase.
 */

@Keep
class ContactDialog extends Dialog {

    private static final int PERMISSION_RETURN_CODE = 112;
    private final Board board;
    private Uri uri;
    private String sortOrder = null;
    private String selection = null;
    private String[] selectionArgs = null;
    public ContactDialog(final Board context) {
        super(context);
        this.board = context;
    }
    /**
     * Instantiates a new utter phrase.
     *
     * @param context the context
     */
    public ContactDialog(final Board context, Intent data) {
        super(context);
        this.board = context;
        this.uri = data.getData();
    }
    public ContactDialog(final Board context, Uri uri, String selection, String[]
            selectionArgs, String sortOrder) {
        super(context);
        this.board = context;
        this.uri = uri;
        this.selection = selection;
        this.selectionArgs = selectionArgs;
        this.sortOrder = sortOrder;
    }

    private Bitmap loadContactPhotoThumbnail(String photoData) {
        // Creates an asset file descriptor for the thumbnail file.
        AssetFileDescriptor afd = null;
        // try-catch block for file not found
        try {
            // Creates a holder for the URI.
            Uri thumbUri;
            thumbUri = Uri.parse(photoData);

            /*
             * Retrieves an AssetFileDescriptor object for the thumbnail
             * URI
             * using ContentResolver.openAssetFileDescriptor
             */
            afd = this.board.getContentResolver().openAssetFileDescriptor(thumbUri, "r");
            if (afd == null) return null;
            /*
             * Gets a file descriptor from the asset file descriptor.
             * This object can be used across processes.
             */
            FileDescriptor fileDescriptor = afd.getFileDescriptor();
            // Decode the photo file and return the result as a Bitmap
            // If the file descriptor is valid
            if (fileDescriptor != null) {
                // Decodes the bitmap
                return BitmapFactory.decodeFileDescriptor(
                        fileDescriptor, null, null);
            }
            // If the file isn't found
        } catch (FileNotFoundException e) {
            /*
             * Handle file not found errors
             */
            // In all cases, close the asset file descriptor
        } finally {
            if (afd != null) {
                try {
                    afd.close();
                } catch (IOException ignored) {
                }
            }
        }
        return null;
    }

    @SuppressLint("Range")
    private ArrayList<Contact> getContacts() {

        ArrayList<Contact> contentList = new ArrayList<>();
        ContentResolver contentResolver = this.board.getContentResolver();
        Cursor cursor = contentResolver.query(this.uri, null, this.selection, this.selectionArgs, this.sortOrder);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                Contact content = new Contact();
                content.ID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                content.LookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                content.Name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                content.PhotoUri = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                content.ContentUri = ContactsContract.Contacts.getLookupUri(
                        cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID)),
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{content.ID}, null);
                    while (phoneCursor != null && phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        int tt = phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        String t = ContactsContract.CommonDataKinds.Phone.getTypeLabel(board.getResources(), tt, "").toString();
                        if (!content.DetailDescriptions.containsKey(t)) {
                            content.DetailDescriptions.put(t, t);
                        }
                        if (!content.Details.containsKey(t)) {
                            content.Details.put(t, new HashMap<>());
                        }
                        HashMap<String, String> key = content.Details.get(t);
                        if (key != null) {
                            key.put("Phone", phoneNumber);
                        }
                    }
                    if (phoneCursor != null) phoneCursor.close();
                }

                Cursor emailCursor = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{content.ID}, null);
                while (emailCursor != null && emailCursor.moveToNext()) {
                    String email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    int tt = emailCursor.getInt(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                    String t = ContactsContract.CommonDataKinds.Email.getTypeLabel(board.getResources(), tt, "").toString();
                    if (!content.DetailDescriptions.containsKey(t)) {
                        content.DetailDescriptions.put(t, t);
                    }
                    if (!content.Details.containsKey(t)) {
                        content.Details.put(t, new HashMap<>());
                    }
                    HashMap<String, String> key = content.Details.get(t);
                    if (key != null) {
                        key.put("eMail", email);
                    }
                }
                if (emailCursor != null) emailCursor.close();

                Cursor addressCursor = contentResolver.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{content.ID}, null);
                while (addressCursor != null && addressCursor.moveToNext()) {
                    int columnIndex = addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS);
                    String address = addressCursor.getString(columnIndex);
                    int tt = addressCursor.getInt(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
                    String t = ContactsContract.CommonDataKinds.StructuredPostal.getTypeLabel(board.getResources(), tt, "").toString();
                    if (!content.DetailDescriptions.containsKey(t)) {
                        content.DetailDescriptions.put(t, t);
                    }
                    if (!content.Details.containsKey(t)) {
                        content.Details.put(t, new HashMap<>());
                    }
                    HashMap<String, String> key = content.Details.get(t);
                    if (key != null) {
                        key.put("Address", address);
                    }
                }
                if (addressCursor != null) addressCursor.close();

                String[] columns = {
                        ContactsContract.CommonDataKinds.Event.START_DATE,
                        ContactsContract.CommonDataKinds.Event.TYPE,
                        ContactsContract.CommonDataKinds.Event.MIMETYPE,
                };
                String where = ContactsContract.CommonDataKinds.Event.TYPE + "=" + ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY +
                        " and " + ContactsContract.CommonDataKinds.Event.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE + "' and " + ContactsContract.Data.CONTACT_ID + " = " + content.ID;
                String sortOrder = ContactsContract.Contacts.DISPLAY_NAME;
                Cursor birthdayCur = contentResolver.query(ContactsContract.Data.CONTENT_URI, columns, where, null, sortOrder);
                if (birthdayCur != null && birthdayCur.getCount() > 0) {
                    while (birthdayCur.moveToNext()) {
                        content.Birthday = birthdayCur.getString(birthdayCur.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
                    }
                }
                if (birthdayCur != null) birthdayCur.close();
                // Add the contactdialog to the ArrayList
                contentList.add(content);
            }
            cursor.close();
        }
        return contentList;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Dialog#onCreate(android.os.Bundle)
     */
    @Override
    protected final void onCreate(final Bundle paramBundle) {
        super.onCreate(paramBundle);
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.contactdialog);
            //setTitle(R.string.contact);
            RecyclerView contactDetails = findViewById(R.id.contact_details);
            contactDetails.setLayoutManager(new LinearLayoutManager(this.board));
            ArrayList<Contact> result = getContacts();
            Contact contact = result.get(0);
            QuickContactBadge badge = findViewById(R.id.contact_badge);
            try {
                badge.assignContactUri(contact.ContentUri);
                Bitmap thumbnailBitmap = loadContactPhotoThumbnail(contact.PhotoUri);
                badge.setImageBitmap(thumbnailBitmap);
            } catch (Exception ex) {
                //OK
            }
            TextView name = findViewById(R.id.contact_name);
            name.setText(contact.Name);
            TextView birthday = findViewById(R.id.contact_birthday);
            birthday.setText(contact.Birthday);


            ContactAdapter adapter = new ContactAdapter(this.board);
            contactDetails.setAdapter(adapter);
            adapter.add(contact);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean requestAppPermission(String[] request) {
        ArrayList<String> askFor = new ArrayList<>();
        for (String r : request) {
            if (ActivityCompat.checkSelfPermission(this.board, r) != PackageManager.PERMISSION_GRANTED)
                askFor.add(r);
        }
        if (askFor.size() > 0) {
            String[] toAskFor = new String[askFor.size()];
            askFor.toArray(toAskFor);
            ActivityCompat.requestPermissions(this.board, toAskFor, ContactDialog.PERMISSION_RETURN_CODE);
            return false;
        }

        return true;
    }

    private static class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {

        private final Context context;
        private HashMap<String, String> dataset;

        DetailAdapter(Context context) {
            this.context = context;
        }

        void add(HashMap<String, String> dataset) {
            this.dataset = dataset;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater == null) return null;
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.contactdetailitem, null, false);
            return new ViewHolder(layout, this.context);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String key = (String) this.dataset.keySet().toArray()[position];
            String data = dataset.get(key);
            holder.data.setText(data);
            holder.label.setText(key);
        }

        @Override
        public int getItemCount() {
            return this.dataset.keySet().toArray().length;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            final TextView label;
            final TextView data;

            ViewHolder(LinearLayout layout, Context ignoredContext) {
                super(layout);
                this.label = layout.findViewById(R.id.detail_label);
                this.data = layout.findViewById(R.id.detail_data);
            }
        }
    }

    private class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

        private final Context context;
        private Contact dataset;

        ContactAdapter(Context context) {
            this.context = context;
        }

        void add(Contact dataset) {
            this.dataset = dataset;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater == null) return null;
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.contactdetails, null, false);
            return new ViewHolder(layout, this.context);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String key = (String) this.dataset.Details.keySet().toArray()[position];
            HashMap<String, String> data = dataset.Details.get(key);
            String label = dataset.DetailDescriptions.get(key);
            holder.label.setText(label);
            DetailAdapter adapter = (DetailAdapter) holder.details.getAdapter();
            if (data != null && adapter != null) {
                holder.email.setVisibility(data.containsKey("eMail") ? View.VISIBLE : View.GONE);
                holder.call.setVisibility(data.containsKey("Phone") ? View.VISIBLE : View.GONE);
                holder.message.setVisibility(data.containsKey("Phone") ? View.VISIBLE : View.GONE);
                holder.map.setVisibility(data.containsKey("Address") ? View.VISIBLE : View.GONE);
                holder.directions.setVisibility(data.containsKey("Address") ? View.VISIBLE : View.GONE);
                holder.email.setOnClickListener(v -> {
                    String[] emails = {data.get("eMail")};
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/html");
                    intent.putExtra(Intent.EXTRA_EMAIL, emails);
                    context.startActivity(Intent.createChooser(intent, "Send Email"));
                });
                holder.call.setOnClickListener(v -> {
                    Uri uri = Uri.parse("tel:" + data.get("Phone"));
                    if (requestAppPermission(new String[]{Manifest.permission.CALL_PHONE})) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    } else {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            context.startActivity(intent);
                        } catch (Exception ex) {
                            Utility.alert(ex.getMessage(), board);
                        }
                    }
                });
                holder.message.setOnClickListener(v -> {
                    Uri uri = Uri.parse("sms:" + data.get("Phone"));
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    } catch (Exception ex) {
                        Utility.alert(ex.getMessage(), board);
                    }
                });
                holder.map.setOnClickListener(v -> {
                    Uri uri = Uri.parse("geo:0,0?q=" + data.get("Address"));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                });
                holder.directions.setOnClickListener(v -> {
                    Uri uri = Uri.parse("google.navigation:?q=" + data.get("Address"));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                });
                adapter.add(data);
            }
        }

        @Override
        public int getItemCount() {
            return this.dataset.Details.keySet().toArray().length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            final TextView label;
            final ImageButton email;
            final ImageButton message;
            final ImageButton call;
            final ImageButton map;
            final ImageButton directions;
            final RecyclerView details;

            ViewHolder(LinearLayout layout, Context context) {
                super(layout);
                this.label = layout.findViewById(R.id.contact_detail_type);
                this.details = layout.findViewById(R.id.details);
                this.email = layout.findViewById(R.id.mail);
                this.call = layout.findViewById(R.id.call);
                this.message = layout.findViewById(R.id.message);
                this.map = layout.findViewById(R.id.map);
                this.directions = layout.findViewById(R.id.directions);
                this.details.setLayoutManager(new LinearLayoutManager(context));
                DetailAdapter detailAdapter = new DetailAdapter(context);
                details.setAdapter(detailAdapter);
            }
        }
    }

}
