import React, { useState, useEffect } from "react";
import {
  uploadFiles,
  updateFiles,
  getAllFiles,
  downloadFile,
  deleteFile,
  uploadAadhar,
  updateAadhar,
  downloadAadhar,
  deleteAadhar,
  uploadPan,
  updatePan,
  downloadPan,
  deletePan,
  uploadExperience,
  updateExperience,
  downloadExperience,
  deleteExperience,
  uploadEducationalDocuments,
  updateEducationalDocuments,
  downloadEducationalDocuments,
  deleteEducationalDocuments,
} from "../../service/documentService";

const DocumentManagement = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [documents, setDocuments] = useState([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [aadharFile, setAadharFile] = useState(null);
  const [panFile, setPanFile] = useState(null);
  const [educationFiles, setEducationFiles] = useState([]);
  const [eduType, setEduType] = useState("");
  const [experienceFiles, setExperienceFiles] = useState([]);
  const [selectedFile, setSelectedFile] = useState(null);
  const [selectedDocType, setSelectedDocType] = useState("");
  const user = JSON.parse(sessionStorage.getItem("user"));
  const employeeId = user?.employeeId;


  // Fetch all documents on component mount
  useEffect(() => {
    const fetchDocuments = async () => {
      try {
        const files = await getAllFiles();
        const filteredFiles = files.filter(file => file.employeeId === employeeId);
        setDocuments([...filteredFiles]);
        console.log("Updated Documents State:", filteredFiles);
      } catch (error) {
        console.error("Failed to fetch documents:", error);
      }
    };
    fetchDocuments();
  }, []);

  // Handle file upload
  const handleUpload = async () => {
    try {
      if (aadharFile) {
        await uploadAadhar(employeeId, aadharFile);
      }
      if (panFile) {
        await uploadPan(employeeId, panFile); 
      }
      if (educationFiles.length > 0) {
        await uploadEducationalDocuments(employeeId, {eduType}, educationFiles);
      }
      if (experienceFiles.length > 0) {
        await uploadExperience(employeeId, experienceFiles[0]);
      }
      const updatedDocuments = await getAllFiles();
      setDocuments(updatedDocuments);
      setAadharFile(null);
      setPanFile(null);
      setEduType("");
      setEducationFiles([]);
      setExperienceFiles([]);
    } catch (error) {
      console.error("Failed to upload files:", error);
    }
  };

  // Handle file update
  const handleUpdate = async (employeeId, type) => {
    try {
      const lowerType = type.toLowerCase(); // Convert to lowercase
      if (lowerType === "aadhar" && selectedFile) {
        await updateAadhar(employeeId, selectedFile);
      } else if (lowerType === "pan" && selectedFile) {
        await updatePan(employeeId, selectedFile);
      } else if (lowerType === "education" && selectedFile) {
        await updateEducationalDocuments(employeeId, eduType, [selectedFile]);
      } else if (lowerType === "experience" && selectedFile) {
        await updateExperience(employeeId, selectedFile);
      } else {
        console.error("No file selected for update or invalid document type.");
        return;
      }
      const updatedDocuments = await getAllFiles();
      setDocuments(updatedDocuments);
      setSelectedFile(null);
      setSelectedDocType("");
    } catch (error) {
      console.error("Failed to update file:", error);
    }
  };
  

  // Handle file download
  const handleDownload = async (employeeId, type) => {
    try {
      const lowerType = type.toLowerCase();
      let fileUrl = "";
      
      if (lowerType === "aadhar") {
        fileUrl = await downloadAadhar(employeeId);
      } else if (lowerType === "pan") {
        fileUrl = await downloadPan(employeeId);
      } else if (lowerType === "education") {
        fileUrl = await downloadEducationalDocuments(employeeId, { eduType });
      } else if (lowerType === "experience") {
        fileUrl = await downloadExperience(employeeId);
      }
  
      if (fileUrl) {
        window.open(fileUrl, "_blank"); // ✅ Open file in new tab
      }
    } catch (error) {
      console.error("Failed to download file:", error);
    }
  };
  

  // Handle file deletion
  const handleDelete = async (employeeId, type) => {
    try {
      const lowerType = type.toLowerCase();
  
      if (lowerType === "aadhar") {
        await deleteAadhar(employeeId);
      } else if (lowerType === "pan") {
        await deletePan(employeeId);
      } else if (lowerType === "education") {
        await deleteEducationalDocuments(employeeId, { eduType });
      } else if (lowerType === "experience") {
        await deleteExperience(employeeId);
      }
  
      // ✅ Remove deleted document from state without refetching all
      setDocuments((prevDocs) =>
        prevDocs.filter((doc) => !(doc.employeeId === employeeId && doc.documentType.toLowerCase() === lowerType))
      );
    } catch (error) {
      console.error("Failed to delete file:", error);
    }
  };
  

  // Filter documents based on search query
  const filteredDocuments = documents.filter((doc) =>
    doc.name?.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">Document Management</h1>

      {/* Search and Upload Button */}
      <div className="flex justify-between mb-4">
        <input
          type="text"
          placeholder="Search by name..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          className="p-2 border rounded w-64"
        />
        <button
          onClick={() => setIsModalOpen(true)}
          className="bg-blue-500 text-white px-4 py-2 rounded"
        >
          Upload Documents
        </button>
      </div>

      {/* Document Table */}
      <table className="w-full border-collapse border">
        <thead>
          <tr className="bg-gray-200">
            <th className="p-2 border">Employee ID</th>
            <th className="p-2 border">Name</th>
            <th className="p-2 border">File</th>
            <th className="p-2 border">Actions</th>
          </tr>
        </thead>
        <tbody>
          {documents.map((doc) => (
            <tr key={doc.id} className="hover:bg-gray-100">
              <td className="p-2 border text-center">{doc.employeeId}</td>
              <td className="p-2 border text-center">{doc.documentType}</td>
              <td className="p-2 border text-center">{doc.fileName}</td>
              <td className="p-2 border text-center">
  <div className="flex flex-col space-y-2 items-center">
    {/* File Input */}
    <label className="cursor-pointer bg-gray-200 text-gray-700 px-3 py-1 rounded-lg hover:bg-gray-300">
      <span>Choose File</span>
      <input
        type="file"
        onChange={(e) => {
          setSelectedFile(e.target.files[0]);
          setSelectedDocType(doc.documentType);
        }}
        className="hidden"
      />
    </label>

    {/* Action Buttons */}
    <div className="flex space-x-2">
      <button
        onClick={() => handleUpdate(doc.employeeId, doc.documentType)}
        className="bg-yellow-500 text-white px-3 py-1 rounded-lg hover:bg-yellow-600 transition duration-200"
      >
        Update
      </button>
      <button
        onClick={() => handleDownload(doc.employeeId, doc.documentType)}
        className="bg-blue-500 text-white px-3 py-1 rounded-lg hover:bg-blue-600 transition duration-200"
      >
        Download
      </button>
      <button
        onClick={() => handleDelete(doc.employeeId, doc.documentType)}
        className="bg-red-500 text-white px-3 py-1 rounded-lg hover:bg-red-600 transition duration-200"
      >
        Delete
      </button>
    </div>
  </div>
</td>
              {/* <td className="p-2 border text-center">
              <input
                  type="file"
                  onChange={(e) => {
                    setSelectedFile(e.target.files[0]);
                    setSelectedDocType(doc.documentType);
                  }}
                  className="mb-2"
                />
                <button
                  onClick={() => handleUpdate(doc.employeeId, doc.documentType)}
                  className="bg-yellow-500 text-white px-2 py-1 rounded mr-2"
                >
                  Update
                </button>
                <button
                  onClick={() => handleDownload(doc.employeeId, doc.documentType)}
                  className="bg-blue-500 text-white px-2 py-1 rounded mr-2"
                >
                  Download
                </button>
                <button
                  onClick={() => handleDelete(doc.employeeId, doc.documentType)}
                  className="bg-red-500 text-white px-2 py-1 rounded"
                >
                  Delete
                </button>
              </td> */}
            </tr>
          ))}
        </tbody>
      </table>

      {/* Upload Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
          <div className="bg-white p-6 rounded-lg w-1/3">
            <h2 className="text-xl font-bold mb-4">Upload Documents</h2>

            {/* Aadhar Upload */}
            <div className="mb-4">
              
              <label className="block mb-2">Aadhar</label>
              <div className="flex space-x-5 mb-4">
              <input
                type="file"
                onChange={(e) => setAadharFile(e.target.files[0])}
                className="w-full p-2 border rounded"
              />
              
              <button
                onClick={handleUpload}
                className="bg-blue-500 text-white px-4 py-2 rounded"
              >
                Upload
              </button>
              </div>
            </div>

            {/* Pan Upload */}
            <div className="mb-4">
            
              <label className="block mb-2">Pan Card</label>
              <div className="flex space-x-5 mb-4">
              <input
                type="file"
                onChange={(e) => setPanFile(e.target.files[0])}
                className="w-full p-2 border rounded"
              />
              
              <button
                onClick={handleUpload}
                className="bg-blue-500 text-white px-4 py-2 rounded"
              >
                Upload
              </button>
              </div>
            </div>

            {/* Education Upload */}
            <div className="mb-4">
              
              <label className="block mb-2">Educational Documents</label>
              
              <input
              type="text"
              value={eduType}
              onChange={(e) => setEduType(e.target.value)}
              placeholder="Enter Education Type"
              className="w-full p-2 border rounded"
            />
            <div className="flex space-x-5 mb-4">
              <input
                type="file"
                multiple
                onChange={(e) => setEducationFiles([...e.target.files])}
                className="w-full p-2 border rounded"
              />
              
              <button
                onClick={handleUpload}
                className="bg-blue-500 text-white px-4 py-2 rounded"
              >
                Upload
              </button>
              </div>
            </div>

            {/* Experience Upload */}
            <div className="mb-4">
             
              <label className="block mb-2">Experience Documents</label>
              <div className="flex space-x-5 mb-4">
              <input
                type="file"
                multiple
                onChange={(e) => setExperienceFiles([...e.target.files])}
                className="w-full p-2 border rounded"
              />
              
              <button
                onClick={handleUpload}
                className="bg-blue-500 text-white px-4 py-2 rounded"
              >
                Upload
              </button>
              </div>
            </div>

            {/* Buttons */}
            <div className="justify-end">
              <button
                onClick={() => setIsModalOpen(false)}
                className="bg-gray-500 text-white px-4 py-2 rounded mr-2"
              >
                Close
              </button>
              
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default DocumentManagement;