import React, { useState, useEffect, createContext, useContext } from 'react';
import { ChevronLeft, ChevronRight, Upload, Download, Trash2, Filter, Plus, Settings, User, LogOut, FileText, Users, Calendar, Search } from 'lucide-react';

// Context for global state management
const AppContext = createContext();

const companyList = [{ "code": "PSFS", "name": "Powerstar Facility Management Service" },
{ "code": "FFMS", "name": "Focus Facility Management Service" },
{ "code": "SFMS", "name": "Shine Facility Management Service" }];


const isEmpty = (val) =>
  val === null || val === undefined || val.toString().trim() === '';

// Real API Service (for App Mode)
class RealApiService {
  static baseUrl = 'http://localhost:8085/psfs-service/api';

  static async login(loginId, password) {

    const apitoken = localStorage.getItem('apitoken');

    const response = await fetch(`${this.baseUrl}/auth/signin`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(apitoken && { 'apitoken': `Bearer ${apitoken}` })
      },
      body: JSON.stringify({ loginId, password })
    });

    if (!response.ok) throw new Error('Login failed');
    return await response.json();
  }

  static async logout() {

    const apitoken = localStorage.getItem('apitoken');

    const response = await fetch(`${this.baseUrl}/auth/signout`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(apitoken && { 'apitoken': `Bearer ${apitoken}` })
      }
    });

    if (!response.ok) throw new Error('Logout failed');
    return await response.json();
  }

  static async register(userData) {

    const response = await fetch(`${this.baseUrl}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(userData)
    });

    if (!response.ok) throw new Error('Registration failed');
    return await response.json();
  }

  static async getUploads(filters = {}) {

    const apitoken = localStorage.getItem('apitoken');

    const response = await fetch(`${this.baseUrl}/uploadList`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(apitoken && { 'apitoken': `Bearer ${apitoken}` })
      },
      body: JSON.stringify(filters)
    });

    if (!response.ok) throw new Error('Failed to fetch uploads');

    return await response.json();
  }

  static async createUpload(uploadData) {

    const apitoken = localStorage.getItem('apitoken');

    const response = await fetch(`${this.baseUrl}/upload`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(apitoken && { 'apitoken': `Bearer ${apitoken}` })
      },
      body: JSON.stringify(uploadData)
    });

    if (!response.ok) throw new Error('Failed to create upload');

    return await response.json();
  }

  static async downloadUpload(filters = {}) {

    const apitoken = localStorage.getItem('apitoken');

    const response = await fetch(`${this.baseUrl}/downloadUpload`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(apitoken && { 'apitoken': `Bearer ${apitoken}` })
      },
      body: JSON.stringify(filters)
    });

    if (!response.ok) throw new Error('Failed to fetch upload download');

    const data = await response.json();

    // Check if data is empty or missing required fields
    if (!data || !data.file_name || !data.file_content) {
      throw new Error('No upload data available for download');
    }

    return data;
  }

  static async getReports(filters = {}) {

    const apitoken = localStorage.getItem('apitoken');

    const response = await fetch(`${this.baseUrl}/salaryList`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(apitoken && { 'apitoken': `Bearer ${apitoken}` })
      },
      body: JSON.stringify(filters)
    });

    if (!response.ok) throw new Error('Failed to fetch reports');

    return await response.json();
  }

  static async downloadSalary(filters = {}) {

    const apitoken = localStorage.getItem('apitoken');

    const response = await fetch(`${this.baseUrl}/downloadSalary`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(apitoken && { 'apitoken': `Bearer ${apitoken}` })
      },
      body: JSON.stringify(filters)
    });

    if (!response.ok) throw new Error('Failed to fetch salary download');

    const data = await response.json();

    // Check if data is empty or missing required fields
    if (!data || !data.file_name || !data.file_content) {
      throw new Error('No salary data available for download');
    }

    return data;
  }

}

// Skeleton Loader Component
const SkeletonLoader = ({ rows = 5 }) => (
  <div className="space-y-3">
    {Array(rows).fill(0).map((_, i) => (
      <div key={i} className="animate-pulse flex space-x-4 p-4 bg-gray-50 rounded">
        <div className="h-4 bg-gray-300 rounded w-1/4"></div>
        <div className="h-4 bg-gray-300 rounded w-1/4"></div>
        <div className="h-4 bg-gray-300 rounded w-1/4"></div>
        <div className="h-4 bg-gray-300 rounded w-1/4"></div>
      </div>
    ))}
  </div>
);

// Login Component
const Login = () => {
  const { login } = useContext(AppContext);
  const [formData, setFormData] = useState({ loginId: '', password: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showRegister, setShowRegister] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {

      await login(formData.loginId, formData.password);

    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async (userData) => {
    setLoading(true);
    setError('');

    try {

      const apiService = RealApiService;
      const result = await apiService.register(userData);
      if ("success" == result.status) {
        setShowRegister(false);
        setFormData({ loginId: userData.loginId, password: '' });
      }

    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  if (showRegister) {
    return <RegisterForm onRegister={handleRegister} onBack={() => setShowRegister(false)} loading={loading} error={error} />;
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            Payslip Management System
          </h2>
          <p className="mt-2 text-center text-sm text-gray-600">
            Sign in to your account
          </p>
        </div>
        <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
          {error && (
            <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
              {error}
            </div>
          )}

          <div className="rounded-md shadow-sm -space-y-px">
            <div>
              <input
                type="loginId"
                required
                className="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-t-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                placeholder="Login ID"
                value={formData.loginId}
                onChange={(e) => setFormData({ ...formData, loginId: e.target.value })}
              />
            </div>
            <div>
              <input
                type="password"
                required
                className="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-b-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                placeholder="Password"
                value={formData.password}
                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
              />
            </div>
          </div>

          <div>
            <button
              type="submit"
              disabled={loading}
              className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
            >
              {loading ? 'Signing in...' : 'Sign in'}
            </button>
          </div>

          <div className="text-center">
            <button
              type="button"
              onClick={() => setShowRegister(true)}
              className="text-indigo-600 hover:text-indigo-500"
            >
              Don't have an account? Register here
            </button>
          </div>

        </form>
      </div>
    </div>
  );
};

// Register Component
const RegisterForm = ({ onRegister, onBack, loading, error }) => {
  const [formData, setFormData] = useState({
    name: '',
    loginId: '',
    password: '',
    confirmPassword: '',
    role: 'Employee'
  });

  const handleSubmit = (e) => {
    e.preventDefault();
    if (formData.password !== formData.confirmPassword) {
      return;
    }
    onRegister(formData);
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            Create Account
          </h2>
        </div>
        <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
          {error && (
            <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
              {error}
            </div>
          )}

          <div className="space-y-4">
            <input
              type="text"
              required
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
              placeholder="Full Name"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            />

            <input
              type="text"
              required
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
              placeholder="Login ID"
              value={formData.loginId}
              onChange={(e) => setFormData({ ...formData, loginId: e.target.value })}
            />

            <select
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
              value={formData.role}
              onChange={(e) => setFormData({ ...formData, role: e.target.value })}
            >
              <option value="Employee">Employee</option>
              <option value="Admin">Admin</option>
            </select>

            <input
              type="password"
              required
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
              placeholder="Password"
              value={formData.password}
              onChange={(e) => setFormData({ ...formData, password: e.target.value })}
            />

            <input
              type="password"
              required
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
              placeholder="Confirm Password"
              value={formData.confirmPassword}
              onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
            />

            {formData.password && formData.confirmPassword && formData.password !== formData.confirmPassword && (
              <p className="text-red-500 text-sm">Passwords do not match</p>
            )}
          </div>

          <div className="flex space-x-4">
            <button
              type="button"
              onClick={onBack}
              className="flex-1 py-2 px-4 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
            >
              Back to Login
            </button>
            <button
              type="submit"
              disabled={loading || formData.password !== formData.confirmPassword}
              className="flex-1 py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
            >
              {loading ? 'Creating...' : 'Create Account'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

// Header Component
const Header = () => {
  const { user, logout, toggleMode } = useContext(AppContext);

  return (
    <header className="bg-white shadow-sm border-b border-gray-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          <div className="flex items-center">
            <FileText className="h-8 w-8 text-indigo-600" />
            <h1 className="ml-2 text-xl font-semibold text-gray-900">Payslip Manager</h1>
          </div>

          <div className="flex items-center space-x-4">
            <div className="flex items-center space-x-2">
              <User className="h-4 w-4 text-gray-500" />
              <span className="text-sm text-gray-700">{user?.name}</span>
              <span className="text-xs text-gray-500 bg-gray-100 px-2 py-1 rounded">
                {user?.role}
              </span>
            </div>

            <button
              onClick={logout}
              className="flex items-center space-x-1 text-gray-600 hover:text-gray-900"
            >
              <LogOut className="h-4 w-4" />
              <span className="text-sm">Logout</span>
            </button>
          </div>
        </div>
      </div>
    </header>
  );
};

// Navigation Component
const Navigation = ({ activeTab, setActiveTab }) => {
  const { user } = useContext(AppContext);

  return (
    <nav className="bg-gray-50 border-b border-gray-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex space-x-8">
          <button
            onClick={() => setActiveTab('home')}
            className={`py-4 px-1 border-b-2 font-medium text-sm ${activeTab === 'home'
              ? 'border-indigo-500 text-indigo-600'
              : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              }`}>
            <div className="flex items-center space-x-2">
              <Upload className="h-4 w-4" />
              <span>Home</span>
            </div>
          </button>

          {user?.id <= 0 && (
            <button
              onClick={() => setActiveTab('login')}
              className={`py-4 px-1 border-b-2 font-medium text-sm ${activeTab === 'login'
                ? 'border-indigo-500 text-indigo-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}>
              <div className="flex items-center space-x-2">
                <Upload className="h-4 w-4" />
                <span>Login</span>
              </div>
            </button>
          )}

          {user?.role === 'Admin' && (
            <button
              onClick={() => setActiveTab('uploads')}
              className={`py-4 px-1 border-b-2 font-medium text-sm ${activeTab === 'uploads'
                ? 'border-indigo-500 text-indigo-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}>
              <div className="flex items-center space-x-2">
                <Upload className="h-4 w-4" />
                <span>Uploads</span>
              </div>
            </button>
          )}

          {user?.role === 'Admin' && (
            <button
              onClick={() => setActiveTab('esiUploads')}
              className={`py-4 px-1 border-b-2 font-medium text-sm ${activeTab === 'esiUploads'
                ? 'border-indigo-500 text-indigo-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}>
              <div className="flex items-center space-x-2">
                <Upload className="h-4 w-4" />
                <span>ESI Uploads</span>
              </div>
            </button>
          )}

          <button
            onClick={() => setActiveTab('reports')}
            className={`py-4 px-1 border-b-2 font-medium text-sm ${activeTab === 'reports'
              ? 'border-indigo-500 text-indigo-600'
              : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              }`}>
            <div className="flex items-center space-x-2">
              <FileText className="h-4 w-4" />
              <span>Reports</span>
            </div>
          </button>
        </div>
      </div>
    </nav>
  );
};

const UploadModal = ({ uploadPurpose, isOpen, onClose, onSubmit, loading }) => {

  // Initialize the state for form data
  const [formData, setFormData] = useState({
    upload_month: '',
    file_name: '',
    file_content: '',
    user_id: '',
    upload_purpose: uploadPurpose
  });

  // Handle the form submission
  const handleSubmit = (e) => {
    e.preventDefault();

    // At this point, file_name and file_content should be properly updated
    console.log(formData); // Verify the formData before submitting

    onSubmit(formData); // Pass the form data to the onSubmit function
  };

  // Handle file input change
  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (!file) return;

    console.log(file); // Log the file for debugging

    // Create a FileReader to read the file's content
    const reader = new FileReader();
    reader.onloadend = () => {
      setFormData((prevFormData) => ({
        ...prevFormData,
        file_name: file.name,   // Update the file_name in state
        file_content: reader.result  // Store the file content as a data URL
      }));
    };

    reader.readAsDataURL(file);  // Read the file as a Data URL
  };

  // Return null if the modal is not open
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
      <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
        <div className="mt-3">
          <h3 className="text-lg font-medium text-gray-900 mb-4">Add New Upload</h3>

          <form onSubmit={handleSubmit} className="space-y-4">
            {uploadPurpose === 'SALARY' && (
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Upload Month
                </label>
                <input
                  type="month"
                  required
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                  value={formData.upload_month}
                  onChange={(e) => setFormData({ ...formData, upload_month: e.target.value })}
                />
              </div>
            )}

            {uploadPurpose === 'ESI' && (
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  User Id
                </label>
                <input
                  type="text"
                  required
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                  value={formData.user_id}
                  onChange={(e) => setFormData({ ...formData, user_id: e.target.value })}
                />
              </div>
            )}

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Upload File
              </label>
              <input
                type="file"
                required
                accept=".xlsx,.xls,.csv,.pdf"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                onChange={handleFileChange}
              />
            </div>

            <div className="flex justify-end space-x-3 pt-4">
              <button
                type="button"
                onClick={onClose}
                className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 border border-gray-300 rounded-md hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={loading}
                className="px-4 py-2 text-sm font-medium text-white bg-indigo-600 border border-transparent rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
              >
                {loading ? 'Uploading...' : 'Upload'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

// Pagination Component
const Pagination = ({ currentPage, totalPages, onPageChange }) => {
  const pages = Array.from({ length: totalPages }, (_, i) => i + 1);

  return (
    <div className="flex items-center justify-between border-t border-gray-200 bg-white px-4 py-3 sm:px-6">
      <div className="flex flex-1 justify-between sm:hidden">
        <button
          onClick={() => onPageChange(currentPage - 1)}
          disabled={currentPage === 1}
          className="relative inline-flex items-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50 disabled:opacity-50"
        >
          Previous
        </button>
        <button
          onClick={() => onPageChange(currentPage + 1)}
          disabled={currentPage === totalPages}
          className="relative ml-3 inline-flex items-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50 disabled:opacity-50"
        >
          Next
        </button>
      </div>
      <div className="hidden sm:flex sm:flex-1 sm:items-center sm:justify-between">
        <div>
          <p className="text-sm text-gray-700">
            Page <span className="font-medium">{currentPage}</span> of{' '}
            <span className="font-medium">{totalPages}</span>
          </p>
        </div>
        <div>
          <nav className="isolate inline-flex -space-x-px rounded-md shadow-sm">
            <button
              onClick={() => onPageChange(currentPage - 1)}
              disabled={currentPage === 1}
              className="relative inline-flex items-center rounded-l-md px-2 py-2 text-gray-400 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 disabled:opacity-50"
            >
              <ChevronLeft className="h-5 w-5" />
            </button>
            {pages.map((page) => (
              <button
                key={page}
                onClick={() => onPageChange(page)}
                className={`relative inline-flex items-center px-4 py-2 text-sm font-semibold ${page === currentPage
                  ? 'z-10 bg-indigo-600 text-white focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600'
                  : 'text-gray-900 ring-1 ring-inset ring-gray-300 hover:bg-gray-50'
                  }`}
              >
                {page}
              </button>
            ))}
            <button
              onClick={() => onPageChange(currentPage + 1)}
              disabled={currentPage === totalPages}
              className="relative inline-flex items-center rounded-r-md px-2 py-2 text-gray-400 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 disabled:opacity-50"
            >
              <ChevronRight className="h-5 w-5" />
            </button>
          </nav>
        </div>
      </div>
    </div>
  );
};

// Uploads Component
const Uploads = () => {
  const [uploads, setUploads] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState({ file_name: '', upload_month: '' });
  const [currentPage, setCurrentPage] = useState(1);
  const [showModal, setShowModal] = useState(false);
  const [uploadLoading, setUploadLoading] = useState(false);
  const [error, setError] = useState('');

  const itemsPerPage = 5;
  const upload_purpose = 'SALARY';

  const fetchUploads = async () => {
    setLoading(true);
    setError('');
    try {
      filters.upload_purpose = upload_purpose;
      const apiService = RealApiService;
      const result = await apiService.getUploads(filters);
      setUploads(result);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUploads();
  }, [filters]);

  const handleUpload = async (uploadData) => {
    setUploadLoading(true);
    setError('');
    try {
      uploadData.upload_purpose = upload_purpose;
      const apiService = RealApiService;
      await apiService.createUpload({
        ...uploadData
      });
      setShowModal(false);
      fetchUploads();
    } catch (err) {
      setError(err.message);
    } finally {
      setUploadLoading(false);
    }
  };

  const handleUploadDownload = async (upload) => {
    setLoading(true);
    setError('');
    try {
      var request = {};
      request.id = upload.id;
      request.upload_purpose = upload_purpose;
      const apiService = RealApiService;
      const result = await apiService.downloadUpload(request);
      handleExportExcel(result);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleExportExcel = (data) => {

    if (isEmpty(data.file_name) && isEmpty(data.file_content)) {
      console.error('Empty or invalid file name or file content.');
      return;
    }

    let base64 = data.file_content;

    // Clean up possible base64 prefix
    if (base64.startsWith('data:')) {
      base64 = base64.split(',')[1];
    }

    // Validate base64 length and characters
    if (!base64 || base64.trim() === '') {
      console.error('Empty or invalid base64 string.');
      return;
    }

    const byteCharacters = atob(base64);
    const byteNumbers = Array.from(byteCharacters, c => c.charCodeAt(0));
    const byteArray = new Uint8Array(byteNumbers);

    let mimeType = 'application/vnd.ms-excel'; // default
    if (data.file_name.toLowerCase().endsWith('.xlsx')) {
      mimeType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
    }
    if (data.file_name.toLowerCase().endsWith('.pdf')) {
      mimeType = 'application/pdf';
    }
    const blob = new Blob([byteArray], { type: mimeType });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = data.file_name
    link.click();
  };

  const filteredUploads = uploads.filter(upload => {
    const matchesName = !filters.file_name || upload.file_name.toLowerCase().includes(filters.file_name.toLowerCase());
    const matchesMonth = !filters.upload_month || upload.upload_month === filters.upload_month;
    return matchesName && matchesMonth;
  });

  const totalPages = Math.ceil(filteredUploads.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const paginatedUploads = filteredUploads.slice(startIndex, startIndex + itemsPerPage);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold text-gray-900">Uploads Management</h2>
          <button
            onClick={() => setShowModal(true)}
            className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            <Plus className="h-4 w-4 mr-2" />
            Add New Upload
          </button>
        </div>

        {error && (
          <div className="mb-4 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
            {error}
          </div>
        )}

        {/* Filters */}
        <div className="bg-white p-4 rounded-lg shadow mb-6">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Filter by Name
              </label>
              <div className="relative">
                <Search className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                <input
                  type="text"
                  className="pl-10 w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                  placeholder="Search uploads..."
                  value={filters.file_name}
                  onChange={(e) => setFilters({ ...filters, file_name: e.target.value })}
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Filter by Month
              </label>
              <div className="relative">
                <Calendar className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                <input
                  type="month"
                  className="pl-10 w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                  value={filters.upload_month}
                  onChange={(e) => setFilters({ ...filters, upload_month: e.target.value })}
                />
              </div>
            </div>

            <div className="flex items-end">
              <button
                onClick={() => {
                  setFilters({ file_name: '', upload_month: '' });
                  setCurrentPage(1);
                }}
                className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 border border-gray-300 rounded-md hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              >
                Clear Filters
              </button>
            </div>
          </div>
        </div>

        {/* Table */}
        <div className="bg-white shadow overflow-hidden sm:rounded-md">
          {loading ? (
            <div className="p-6">
              <SkeletonLoader rows={5} />
            </div>
          ) : (
            <>
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Upload Month
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      File Name
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Upload Date
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Actions
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {paginatedUploads.length === 0 ? (
                    <tr>
                      <td colSpan="5" className="px-6 py-4 text-center text-gray-500">
                        No uploads found
                      </td>
                    </tr>
                  ) : (
                    paginatedUploads.map((upload) => (
                      <tr key={upload.id} className="hover:bg-gray-50">
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                          {new Date(upload.upload_month + '-01').toLocaleDateString('en-US', {
                            year: 'numeric',
                            month: 'long'
                          })}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                          {upload.file_name}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                          {new Date(upload.upload_date).toLocaleDateString()}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                          <div className="flex space-x-2">
                            <button
                              onClick={() => handleUploadDownload(upload)}
                              className="inline-flex items-center px-3 py-1 border border-transparent text-xs font-medium rounded text-indigo-600 bg-indigo-100 hover:bg-indigo-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                            >
                              <Download className="h-3 w-3 mr-1" />
                              Download
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>

              {totalPages > 1 && (
                <Pagination
                  currentPage={currentPage}
                  totalPages={totalPages}
                  onPageChange={setCurrentPage}
                />
              )}
            </>
          )}
        </div>
      </div>

      <UploadModal
        uploadPurpose={upload_purpose}
        isOpen={showModal}
        onClose={() => setShowModal(false)}
        onSubmit={handleUpload}
        loading={uploadLoading}
      />
    </div>
  );
};

// ESIUploads Component 
const ESIUploads = () => {
  const [uploads, setUploads] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState({ file_name: '', user_id: '' });
  const [currentPage, setCurrentPage] = useState(1);
  const [showModal, setShowModal] = useState(false);
  const [uploadLoading, setUploadLoading] = useState(false);
  const [error, setError] = useState('');

  const itemsPerPage = 5;
  const upload_purpose = 'ESI';

  const fetchUploads = async () => {
    setLoading(true);
    setError('');
    try {

      filters.upload_purpose = upload_purpose;
      const apiService = RealApiService;
      const result = await apiService.getUploads(filters);
      setUploads(result);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUploads();
  }, [filters]);

  const handleUpload = async (uploadData) => {
    setUploadLoading(true);
    setError('');
    try {
      uploadData.upload_purpose = upload_purpose;
      const apiService = RealApiService;
      await apiService.createUpload({
        ...uploadData
      });
      setShowModal(false);
      fetchUploads();
    } catch (err) {
      setError(err.message);
    } finally {
      setUploadLoading(false);
    }
  };

  const handleUploadDownload = async (upload) => {
    setLoading(true);
    setError('');
    try {
      var request = {};
      request.id = upload.id;
      request.upload_purpose = upload_purpose;
      const apiService = RealApiService;
      const result = await apiService.downloadUpload(request);
      handleExportExcel(result);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleExportExcel = (data) => {

    if (isEmpty(data.file_name) && isEmpty(data.file_content)) {
      console.error('Empty or invalid file name or file content.');
      return;
    }

    let base64 = data.file_content;

    // Clean up possible base64 prefix
    if (base64.startsWith('data:')) {
      base64 = base64.split(',')[1];
    }

    // Validate base64 length and characters
    if (!base64 || base64.trim() === '') {
      console.error('Empty or invalid base64 string.');
      return;
    }

    const byteCharacters = atob(base64);
    const byteNumbers = Array.from(byteCharacters, c => c.charCodeAt(0));
    const byteArray = new Uint8Array(byteNumbers);

    let mimeType = 'application/vnd.ms-excel'; // default
    if (data.file_name.toLowerCase().endsWith('.xlsx')) {
      mimeType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
    }
    if (data.file_name.toLowerCase().endsWith('.pdf')) {
      mimeType = 'application/pdf';
    }
    const blob = new Blob([byteArray], { type: mimeType });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = data.file_name
    link.click();
  };

  const filteredUploads = uploads.filter(upload => {
    const matchesName = !filters.file_name || upload.file_name.toLowerCase().includes(filters.file_name.toLowerCase());
    const matchesMonth = !filters.user_id || upload.user_id === filters.user_id;
    return matchesName && matchesMonth;
  });

  const totalPages = Math.ceil(filteredUploads.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const paginatedUploads = filteredUploads.slice(startIndex, startIndex + itemsPerPage);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold text-gray-900">Uploads Management</h2>
          <button
            onClick={() => setShowModal(true)}
            className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            <Plus className="h-4 w-4 mr-2" />
            Add New Upload
          </button>
        </div>

        {error && (
          <div className="mb-4 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
            {error}
          </div>
        )}

        {/* Filters */}
        <div className="bg-white p-4 rounded-lg shadow mb-6">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Filter by Name
              </label>
              <div className="relative">
                <Search className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                <input
                  type="text"
                  className="pl-10 w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                  placeholder="Search uploads..."
                  value={filters.file_name}
                  onChange={(e) => setFilters({ ...filters, file_name: e.target.value })}
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Filter by User
              </label>
              <div className="relative">
                <Calendar className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                <input
                  type="text"
                  className="pl-10 w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                  value={filters.user_id}
                  onChange={(e) => setFilters({ ...filters, user_id: e.target.value })}
                />
              </div>
            </div>

            <div className="flex items-end">
              <button
                onClick={() => {
                  setFilters({ file_name: '', user_id: '' });
                  setCurrentPage(1);
                }}
                className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 border border-gray-300 rounded-md hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              >
                Clear Filters
              </button>
            </div>
          </div>
        </div>

        {/* Table */}
        <div className="bg-white shadow overflow-hidden sm:rounded-md">
          {loading ? (
            <div className="p-6">
              <SkeletonLoader rows={5} />
            </div>
          ) : (
            <>
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Upload Month
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      File Name
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Upload Date
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Actions
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {paginatedUploads.length === 0 ? (
                    <tr>
                      <td colSpan="5" className="px-6 py-4 text-center text-gray-500">
                        No uploads found
                      </td>
                    </tr>
                  ) : (
                    paginatedUploads.map((upload) => (
                      <tr key={upload.id} className="hover:bg-gray-50">
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                          {upload.user_id}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                          {upload.file_name}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                          {new Date(upload.upload_date).toLocaleDateString()}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                          <div className="flex space-x-2">
                            <button
                              onClick={() => handleUploadDownload(upload)}
                              className="inline-flex items-center px-3 py-1 border border-transparent text-xs font-medium rounded text-indigo-600 bg-indigo-100 hover:bg-indigo-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                            >
                              <Download className="h-3 w-3 mr-1" />
                              Download
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>

              {totalPages > 1 && (
                <Pagination
                  currentPage={currentPage}
                  totalPages={totalPages}
                  onPageChange={setCurrentPage}
                />
              )}
            </>
          )}
        </div>
      </div>

      <UploadModal
        uploadPurpose={upload_purpose}
        isOpen={showModal}
        onClose={() => setShowModal(false)}
        onSubmit={handleUpload}
        loading={uploadLoading}
      />
    </div>
  );
};

// Reports Component
const Reports = () => {
  const { user } = useContext(AppContext);
  const [reports, setReports] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState({ id_no: '', uan_no: '', upload_month: '' });
  const [currentPage, setCurrentPage] = useState(1);
  const [error, setError] = useState('');
  const itemsPerPage = 5;

  const fetchReports = async () => {
    setLoading(true);
    setError('');
    try {
      const apiService = RealApiService;
      const result = await apiService.getReports(filters);
      setReports(result);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReports();
  }, [filters]);

  const handleReportDownload = async (report) => {
    setLoading(true);
    setError('');
    try {
      var request = {};
      request.id_no = report.id_no;
      const apiService = RealApiService;
      const result = await apiService.downloadSalary(request);
      handleExportPDF(result);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleExportPDF = (data) => {

    if (isEmpty(data.file_name) && isEmpty(data.file_content)) {
      console.error('Empty or invalid file name or file content.');
      return;
    }

    // Strip base64 prefix if present
    const base64 = data.file_content.split(',').pop();

    // Convert base64 to binary
    const byteCharacters = atob(base64);
    const byteNumbers = Array.from(byteCharacters, c => c.charCodeAt(0));
    const byteArray = new Uint8Array(byteNumbers);

    const blob = new Blob([byteArray], { type: 'application/pdf' });

    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = data.file_name;
    link.click();
  };

  const filteredReports = reports.filter(report => {
    const matchesId = !filters.id_no || report.id_no.toLowerCase().includes(filters.id_no.toLowerCase());
    const matchesUan = !filters.uan_no || report.uan_no.toLowerCase().includes(filters.uan_no.toLowerCase());
    const matchesMonth = !filters.upload_month || report.upload_month === filters.upload_month;
    return matchesId && matchesUan && matchesMonth;
  });

  const totalPages = Math.ceil(filteredReports.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const paginatedReports = filteredReports.slice(startIndex, startIndex + itemsPerPage);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold text-gray-900">Reports</h2>
        </div>

        {error && (
          <div className="mb-4 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
            {error}
          </div>
        )}

        {/* Filters */}
        <div className="bg-white p-4 rounded-lg shadow mb-6">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Filter by ID
              </label>
              <div className="relative">
                <Search className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                <input
                  type="text"
                  className="pl-10 w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                  placeholder="Search by id..."
                  value={filters.id_no}
                  onChange={(e) => setFilters({ ...filters, id_no: e.target.value })}
                />
              </div>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Filter by UAN
              </label>
              <div className="relative">
                <Search className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                <input
                  type="text"
                  className="pl-10 w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                  placeholder="Search by UAN..."
                  value={filters.uan_no}
                  onChange={(e) => setFilters({ ...filters, uan_no: e.target.value })}
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Filter by Month
              </label>
              <div className="relative">
                <Calendar className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                <input
                  type="month"
                  className="pl-10 w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                  value={filters.upload_month}
                  onChange={(e) => setFilters({ ...filters, upload_month: e.target.value })}
                />
              </div>
            </div>


            <div className="flex items-end">
              <button
                onClick={() => {
                  setFilters({ id_no: '', uan_no: '', upload_month: '' });
                  setCurrentPage(1);
                }}
                className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 border border-gray-300 rounded-md hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              >
                Clear Filters
              </button>
            </div>
          </div>
        </div>

        {/* Table */}
        <div className="bg-white shadow overflow-hidden sm:rounded-md">
          {loading ? (
            <div className="p-6">
              <SkeletonLoader rows={5} />
            </div>
          ) : (
            <>
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Id No
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      UAN
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Employee Name
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Actions
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {paginatedReports.length === 0 ? (
                    <tr>
                      <td className="px-6 py-4 text-center text-gray-500">
                        No reports found
                      </td>
                    </tr>
                  ) : (
                    paginatedReports.map((report) => (
                      <tr key={report.id} className="hover:bg-gray-50">
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                          {report.id_no}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                          {report.uan_no}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                          {report.name_of_the_employee}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                          <button
                            onClick={() => handleReportDownload(report)}
                            className="inline-flex items-center px-3 py-1 border border-transparent text-xs font-medium rounded text-indigo-600 bg-indigo-100 hover:bg-indigo-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                          >
                            <Download className="h-3 w-3 mr-1" />
                            Download
                          </button>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>

              {totalPages > 1 && (
                <Pagination
                  currentPage={currentPage}
                  totalPages={totalPages}
                  onPageChange={setCurrentPage}
                />
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
};


// Home Component

// Import images from the local assets folder
import driverImg from './assets/img/driverImg.jpg';
import officeFemaleImg from './assets/img/officeFemaleImg.jpg';
import shoppingImg from './assets/img/shoppingImg.jpg';
import technicianImg from './assets/img/technicianImg.jpg';
import restaurantImg from './assets/img/restaurantImg.jpg';
import warehouseImg from './assets/img/warehouseImg.jpg';
import corporateImg from './assets/img/corporateImg.jpg';
import officeImg from './assets/img/officeImg.jpg';

const Home = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Extended services list with 20 services
  const services = [
    {
      name: "Driver Facility Service",
      imageUrl: driverImg,
      description: "We are master in offering best in class services therefore we offer one of the best Driver Facility Service. Optimum functionality and on time execution of our service has increased its craze among our valued customers. Superb smooth working and high performance of our service is highly liked among the customers. Additionally, these can be availed from us at reasonable price."

    },
    {
      name: "Office Female Assistant Service",
      imageUrl: officeFemaleImg,
      description: "We hold immense recognition in this sector and are specialized in offering a range of Office Female Assistant Service. This service is completed by highly experienced and trained professionals. We provide these services in compliance with the requirements made by the customers. Additionally, these can be availed from us at reasonable price."
    },
    {
      name: "Shopping Mall Security Service",
      imageUrl: shoppingImg,
      description: "We are betrothed throughout providing Shopping Mall Security Services to our customers. We have hired best out of our lot in presenting these services effectively. We are known for reasonable rates and reliability. Our Shopping Mall Security Service are highly accredited and accepted in the industry. Additionally, Our services are superb and our prices are affordable."
    },
    {
      name: "Technician Support Service",
      imageUrl: technicianImg,
      description: "We are amongst the most reliable companies included in providing a far reaching scope of Technician Support Services. We have a group of exceptionally gifted experts who completed these Technician Support Services in a smooth way to render greatest customer fulfillment. Additionally, Our services are superb and our prices are affordable."
    },
    {
      name: "Restaurant Housekeeping Service",
      imageUrl: restaurantImg,
      description: "Our firm is just about the leading companies in offering Restaurant Housekeeping Service to customers. The service offers of this service is offered training and detailed understanding of these services. This Restaurant Housekeeping Service is offered at reasonable price and highly acclaimed from the valuable customers. Additionally, Our services are superb and our prices are affordable."
    },
    {
      name: "Warehouse Housekeeping Services",
      imageUrl: warehouseImg,
      description: "We are trusted firm for presenting Warehouse Housekeeping Services. These services are well known for their features like reliability, flexibility and timeliness. High quality machines and tools utilized by our teama s people while rendering these services. Apart from this, we offer these Warehouse Housekeeping Services at market leading prices for our customers."
    },
    {
      name: "Corporate Housekeeping Service - Global Excellence Standards",
      imageUrl: corporateImg,
      description: "We are famous brand for offering Corporate Housekeeping Services. Our offered services are widely praised by our customers which are located all round the world. Additionally, Our services are superb and our prices are affordable."
    },
    {
      name: "Office Housekeeping Service - Superior Quality Management",
      imageUrl: officeImg,
      description: "our firm shares trust and quality with his customers in providing the highest quality Office Housekeeping Service. We are backed up by the team of specially skilled professionals, under whose supervision the service is executed for making it flawless. Our Office Housekeeping Service is confident of optimum standard class and comes in a wide range for selection. Additionally, Our services are superb and our prices are affordable."
    }
  ];

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="w-full space-y-8">

        {/* About Power Star Facility Services */}
        <div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            About Power Star Facility Services
          </h2>
          <p className="mt-2 text-center text-sm text-gray-600">
            Registered in 2015, Power Star Facility Services has made a name for itself as one of the top service providers of office boy services and hotel housekeeping services in the country. Power Star Facility Services is listed in Trade India's list of verified companies offering a wide array of Industrial Housekeeping Services, Hotel Housekeeping Services, and more. Contact us for office boy services and hotel housekeeping services in Chennai, Tamil Nadu.
          </p>
        </div>

        {/* Services Section */}
        <div className="mt-12 space-y-12">
          <h3 className="text-xl font-semibold text-gray-900 text-center">Our Services</h3>

          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-2 gap-6 mt-6">
            {services.map((service, index) => (
              <div key={index} className="border rounded-md p-4 shadow-lg bg-white text-center">
                <img src={service.imageUrl} alt={service.name} className="w-32 h-32 object-cover mx-auto mb-4 rounded-md" />
                <h4 className="text-lg font-medium text-gray-900">{service.name}</h4>
                <p className="text-sm text-gray-600">{service.description}</p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

// Main Dashboard Component
const Dashboard = () => {
  const { user } = useContext(AppContext);
  const [activeTab, setActiveTab] = useState('home');

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />

      {/* Navigation */}
      <ul className="flex space-x-4 p-4 bg-white shadow">
        <li onClick={() => setActiveTab('home')}>Home</li>
        <li onClick={() => setActiveTab('contact')}>Contact</li>
        <li onClick={() => setActiveTab('payroll')}>Payroll</li>

        {user?.role === 'Admin' && (
          <>
            <li onClick={() => setActiveTab('uploads')}>Uploads</li>
            <li onClick={() => setActiveTab('esiUploads')}>ESI Uploads</li>
          </>
        )}

        {user && <li onClick={() => setActiveTab('reports')}>Reports</li>}
        {!user && <li onClick={() => setActiveTab('login')}>Login</li>}
      </ul>

      {/* Page Content */}
      <main className="p-4">
        {activeTab === 'home' && <Home />}
        {activeTab === 'contact' && <Contact />}
        {activeTab === 'payroll' && <Payroll />}
        {user?.role === 'Admin' && activeTab === 'uploads' && <Uploads />}
        {user?.role === 'Admin' && activeTab === 'esiUploads' && <ESIUploads />}
        {user && activeTab === 'reports' && <Reports />}
        {activeTab === 'login' && !user && <Login />}
      </main>
    </div>
  );
};


// Main App Component
const App = () => {
  const [user, setUser] = useState(null);

  const login = async (loginId, password) => {
    const apiService = RealApiService;
    const result = await apiService.login(loginId, password);
    if (result.status === "success") {
      localStorage.setItem("apitoken", result.jwtToken);
      setUser(result);
    }
  };

  const logout = async () => {
    const apiService = RealApiService;
    const result = await apiService.logout();
    if (result.status === "success") {
      localStorage.removeItem("apitoken");
      setUser(null);
    }
  };

  const contextValue = { user, login, logout };

  return (
    <AppContext.Provider value={contextValue}>
      <Dashboard />
    </AppContext.Provider>
  );
};


export default App;