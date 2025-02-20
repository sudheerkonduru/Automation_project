const documentManagementLink = import.meta.env.VITE_DOCUMENT_MANAGEMENT;
const BASE_URL = `${documentManagementLink}/api/v1/files`;

// Helper function to handle API responses
const handleResponse = async (response) => {
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.error || "Request failed");
  }
  return response.json();
};

// Helper function to handle file downloads
const handleFileDownload = async (response, defaultFilename) => {
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.error || "Failed to download file");
  }

  const blob = await response.blob();
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = defaultFilename;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
};

// ------------------ Common Endpoints ------------------

export const uploadFiles = async (employeeId, files) => {
  const formData = new FormData();
  files.forEach((file) => formData.append("files", file));

  const response = await fetch(`${BASE_URL}/uploadFiles/${employeeId}`, {
    method: "POST",
    body: formData,
  });

  return handleResponse(response);
};

export const updateFiles = async (employeeId, files) => {
  const formData = new FormData();
  files.forEach((file) => formData.append("files", file));

  const response = await fetch(`${BASE_URL}/updateFiles/${employeeId}`, {
    method: "PUT",
    body: formData,
  });

  return handleResponse(response);
};

export const getAllFiles = async () => {
  const response = await fetch(`${BASE_URL}/getAllFiles`);
  return handleResponse(response);
};

export const downloadFile = async (employeeId) => {
  const response = await fetch(`${BASE_URL}/download/${employeeId}`);
  await handleFileDownload(response, "downloaded_file");
};

export const deleteFile = async (employeeId) => {
  const response = await fetch(`${BASE_URL}/deleteFile/${employeeId}`, {
    method: "DELETE",
  });

  return handleResponse(response);
};

// ------------------ Aadhar Endpoints ------------------

export const uploadAadhar = async (employeeId, file) => {
  const formData = new FormData();
  formData.append("file", file);

  const response = await fetch(`${BASE_URL}/uploadAadhar/${employeeId}`, {
    method: "POST",
    body: formData,
  });

  return handleResponse(response);
};

export const updateAadhar = async (employeeId, file) => {
  const formData = new FormData();
  formData.append("file", file);

  const response = await fetch(`${BASE_URL}/updateAadhar/${employeeId}`, {
    method: "PUT",
    body: formData,
  });

  return handleResponse(response);
};

export const downloadAadhar = async (employeeId) => {
  const response = await fetch(`${BASE_URL}/downloadAadhar/${employeeId}`);
  await handleFileDownload(response, "aadhar_document");
};

export const deleteAadhar = async (employeeId) => {
  const response = await fetch(`${BASE_URL}/deleteAadhar/${employeeId}`, {
    method: "DELETE",
  });

  return handleResponse(response);
};

// ------------------ PAN Endpoints ------------------

export const uploadPan = async (employeeId, file) => {
  const formData = new FormData();
  formData.append("file", file);

  const response = await fetch(`${BASE_URL}/uploadPan/${employeeId}`, {
    method: "POST",
    body: formData,
  });

  return handleResponse(response);
};

export const updatePan = async (employeeId, file) => {
  const formData = new FormData();
  formData.append("file", file);

  const response = await fetch(`${BASE_URL}/updatePan/${employeeId}`, {
    method: "PUT",
    body: formData,
  });

  return handleResponse(response);
};

export const downloadPan = async (employeeId) => {
  const response = await fetch(`${BASE_URL}/downloadPan/${employeeId}`);
  await handleFileDownload(response, "pan_document");
};

export const deletePan = async (employeeId) => {
  const response = await fetch(`${BASE_URL}/deletePan/${employeeId}`, {
    method: "DELETE",
  });

  return handleResponse(response);
};

// ------------------ Experience Endpoints ------------------

export const uploadExperience = async (employeeId, file) => {
  const formData = new FormData();
  formData.append("file", file);

  const response = await fetch(`${BASE_URL}/uploadExperience/${employeeId}`, {
    method: "POST",
    body: formData,
  });

  return handleResponse(response);
};

export const updateExperience = async (employeeId, file) => {
  const formData = new FormData();
  formData.append("file", file);
  const response = await fetch(`${BASE_URL}/updateExperience/${employeeId}`, {
    method: "PUT",
    body: formData,
  });
  return handleResponse(response);
};

export const downloadExperience = async (employeeId) => {
  const response = await fetch(`${BASE_URL}/downloadExperience/${employeeId}`);
  await handleFileDownload(response, "experience_document");
};

export const deleteExperience = async (employeeId) => {
  const response = await fetch(`${BASE_URL}/deleteExperience/${employeeId}`, {
    method: "DELETE",
  });

  return handleResponse(response);
};

// ------------------ Educational Documents Endpoints ------------------

export const uploadEducationalDocuments = async (employeeId, eduType, files) => {
  const formData = new FormData();
  files.forEach((file) => formData.append("files", file));

  const response = await fetch(
    `${BASE_URL}/uploadEducationalDocuments/${employeeId}/${eduType}`,
    {
      method: "POST",
      body: formData,
    }
  );

  return handleResponse(response);
};

export const updateEducationalDocuments = async (employeeId, eduType, files) => {
  const formData = new FormData();
  formData.append("eduType", eduType);
  files.forEach((file, index) => {
    formData.append(`files[${index}]`, file);
  });
  const response = await fetch(`${BASE_URL}/updateEducation/${employeeId}`, {
    method: "PUT",
    body: formData,
  });
  return handleResponse(response);
};

export const downloadEducationalDocuments = async (employeeId, eduType) => {
  const response = await fetch(
    `${BASE_URL}/downloadEducationalDocuments/${employeeId}/${eduType}`
  );
  return handleResponse(response);
};

export const deleteEducationalDocuments = async (employeeId, eduType) => {
  const response = await fetch(
    `${BASE_URL}/deleteEducationalDocuments/${employeeId}/${eduType}`,
    {
      method: "DELETE",
    }
  );

  return handleResponse(response);
};
