package com.rameshmklll.church;

import android.app.Dialog;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.rameshmklll.church.adapters.TeluguBibleAdapter;
import com.rameshmklll.church.pojos.TeluguBiblePojo;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the

 * to handle interaction events.
 * Use the {@link BibleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BibleFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    AssetManager assetManager;
    private HSSFRow myRow;
    String chapter="2.0",version="1",book_name="Genesis";
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    View view;
    private static ArrayList<TeluguBiblePojo> data;
    private static RecyclerView.Adapter adapter;
    public BibleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BibleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BibleFragment newInstance(String param1, String param2) {
        BibleFragment fragment = new BibleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);


      //  recyclerView.setHasFixedSize(true);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_bible, container, false);

        return  view;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView = (RecyclerView)view. findViewById(R.id.my_recycler_view);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        data = new ArrayList<TeluguBiblePojo>();
        adapter = new TeluguBibleAdapter(data);
        readExcelFileFromAssets();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch(id){
            case 2:
                final Spinner spVersions,spChapters;
                Button btSearch;
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.bible_search_layout);
                spVersions=(Spinner) dialog.findViewById(R.id.spVersion);
                spChapters=(Spinner) dialog.findViewById(R.id.spChapter);
                btSearch=(Button) dialog.findViewById(R.id.btnReadExcel1);
                btSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       chapter=spChapters.getSelectedItem().toString();
                        version=spVersions.getSelectedItem().toString();
                        readExcelFileFromAssets();
                    }
                });
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void readExcelFileFromAssets() {

        try {
            // Creating Input Stream
   /*
    * File file = new File( filename); FileInputStream myInput = new
    * FileInputStream(file);
    */

            InputStream myInput;
            assetManager=getActivity().getAssets();

            //  Don't forget to Change to your assets folder excel sheet
            myInput = assetManager.open("bible.xls");

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            /** We now need something to iterate through the cells. **/
            Iterator<Row> rowIter = mySheet.rowIterator();
            rowIter.next();

            while (rowIter.hasNext()) {
               // HSSFRow  HSSFRowmyRow = (HSSFRow) rowIter.next();
                myRow = (HSSFRow) rowIter.next();
                Cell cell_book_name=myRow.getCell(0);
                Cell cell_chapter=myRow.getCell(1);
                Cell cell_version=myRow.getCell(3);
                if( String.valueOf(cell_book_name.getStringCellValue()).equalsIgnoreCase(book_name)  &&  String.valueOf(cell_chapter.getNumericCellValue()).equalsIgnoreCase(chapter) ){
                    Cell cell_content=myRow.getCell(2);
                    String content=String.valueOf(cell_content.getStringCellValue());
                    String id= String.valueOf(cell_version.getNumericCellValue());
                    Log.i("chapters", String.valueOf(cell_chapter.getNumericCellValue()));
                    TeluguBiblePojo teluguBiblePojo=new TeluguBiblePojo(content,id);
                    data.add(teluguBiblePojo);
                }


                Iterator<Cell> cellIter = myRow.cellIterator();
//                while (cellIter.hasNext()) {
//                    HSSFCell myCell = (HSSFCell) cellIter.next();
//                  Log.i( "cell content", myCell.getStringCellValue()  );
//                    Log.e("FileUtils", "Cell Value: " + myCell.toString()+ " Index :" +myCell.getColumnIndex());
//                    // Toast.makeText(getApplicationContext(), "cell Value: " +
//                    // myCell.toString(), Toast.LENGTH_SHORT).show();
//                }
            }
          adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0,2,1,"Search").setIcon(R.drawable.ic_search).setShowAsAction(1);
    }
}
